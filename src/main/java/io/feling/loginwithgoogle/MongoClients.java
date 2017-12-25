package io.feling.loginwithgoogle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


@Slf4j
public class MongoClients {

    private static Cache<String, MongoCollection> mongoCollectionCache = CacheBuilder
            .newBuilder()
            .maximumSize(512)
            .build();

    private static class MongoClientHolder {

        private static Properties properties = new Properties();

        static {
            try (InputStream mongoProperties = MongoClients.class.getResourceAsStream("/mongo.properties")) {
                if (mongoProperties != null) {
                    properties.load(mongoProperties);
                } else {
                    log.error("MongoClients 加载 mongo.properties 发生异常");
                    throw new RuntimeException("MongoClients 加载 mongo.properties 发生异常");
                }
            } catch (IOException e) {
                log.error("MongoClients 加载 mongo.properties 发生异常");
                throw new RuntimeException("MongoClients 加载 mongo.properties 发生异常");
            }
        }

        private static CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(new JacksonCodecProvider(ObjectMapperFactory.createObjectMapperWithSerializeNulls())));

        private static MongoClientURI mongoClientURI = new MongoClientURI(properties.getProperty("mongo.uri"),
                MongoClientOptions.builder().codecRegistry(codecRegistry));

        private static MongoClient mongoClient = new MongoClient(mongoClientURI);

        private static MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());

    }

    public MongoClients() {
    }

    public static <TDocument> MongoCollection<TDocument> getCollection(String collectionName, Class<TDocument> clazz) {
        try {
            return mongoCollectionCache.get(collectionName, () -> MongoClientHolder.mongoDatabase.getCollection(collectionName, clazz));
        } catch (ExecutionException e) {
            return MongoClientHolder.mongoDatabase.getCollection(collectionName, clazz);
        }
    }


}
