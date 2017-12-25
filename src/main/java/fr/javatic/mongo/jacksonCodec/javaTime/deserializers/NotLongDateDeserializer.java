package fr.javatic.mongo.jacksonCodec.javaTime.deserializers;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.undercouch.bson4jackson.BsonConstants;
import de.undercouch.bson4jackson.BsonParser;
import de.undercouch.bson4jackson.deserializers.BsonDeserializer;

import java.io.IOException;
import java.util.Date;

public class NotLongDateDeserializer extends BsonDeserializer<Date> {
    @Override
    public Date deserialize(BsonParser bsonParser, DeserializationContext ctxt) throws IOException {
        if (bsonParser.getCurrentToken() != JsonToken.VALUE_EMBEDDED_OBJECT ||
                bsonParser.getCurrentBsonType() != BsonConstants.TYPE_DATETIME) {
            throw ctxt.mappingException(Date.class);
        }

        Object obj = bsonParser.getEmbeddedObject();
        return (Date) obj;
    }
}