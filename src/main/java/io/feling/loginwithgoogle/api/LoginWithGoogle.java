package io.feling.loginwithgoogle.api;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import io.feling.loginwithgoogle.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.Date;
import java.util.UUID;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

@Slf4j
public class LoginWithGoogle implements PojoRequestHandler<APIGatewayRequest, APIGatewayResponse> {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    public APIGatewayResponse handleRequest(APIGatewayRequest input, Context context) {
        try {
            String httpResult = get("https://proxy.feling.io/www.googleapis.com/oauth2/v3/tokeninfo?id_token="
                    + input.getQueryParameters().get("idToken"));

            TokenInfo tokeninfo = gson.fromJson(httpResult, TokenInfo.class);
            if (!"accounts.google.com".equals(tokeninfo.getIss())
                    && !"https://accounts.google.com".equals(tokeninfo.getIss())) {
                throw new RuntimeException("认证失败.");
            }

            if (!"1093205252929-cm3ev302entgv6kutudb7i077je31g3r.apps.googleusercontent.com".equals(tokeninfo.getAud())) {
                throw new RuntimeException("认证失败..");
            }

            if (System.currentTimeMillis() > Long.valueOf(tokeninfo.getExp()) * 1000) {
                throw new RuntimeException("认证失败...");
            }

            String accessToken = UUID.randomUUID().toString().replace("-", "");

            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
            options.upsert(true);
            MongoClients.getCollection("userInfo", UserInfo.class)
                    .findOneAndUpdate(
                            and(
                                    eq("isDel", 0),
                                    eq("userId", tokeninfo.getSub())
                            ),
                            combine(
                                    set("userEmail", tokeninfo.getEmail()),
                                    set("userEmailVerified", "true".equals(tokeninfo.getEmailVerified()) ? true : false),
                                    set("userName", tokeninfo.getName()),
                                    set("givenName", tokeninfo.getGivenName()),
                                    set("familyName", tokeninfo.getFamilyName()),
                                    set("userAvatar", tokeninfo.getPicture()),
                                    set("accessToken", accessToken),
                                    set("accessTokenExpiredAt", new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24))),
                                    currentDate("lastModified"),
                                    setOnInsert("firstCreated", new Date())
                            ),
                            options
                    );

            return new APIGatewayResponse(
                    new Response().data(
                            ImmutableMap.of(
                                    "accessToken", accessToken,
                                    "userName", tokeninfo.getName(),
                                    "userAvatar", tokeninfo.getPicture()
                            )
                    ).toString()
            );

        } catch (Exception e) {
            return new APIGatewayResponse(new Response().fail().errCode("E000001").msg(e.getMessage()).toString());
        }
    }

    public static String get(String url) {

        String result = null;
        HttpEntity entity = null;
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            get.setConfig(RequestConfig.custom()
                    .setConnectTimeout(3000)
                    .setSocketTimeout(10 * 1000)
                    .build());

            CloseableHttpResponse response = HttpClientBuilder.create().build().execute(get);
            entity = response.getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }

        } catch (Exception e) {

        } finally {
            EntityUtils.consumeQuietly(entity);
        }
        return result;
    }

}
