package io.feling.loginwithgoogle.api;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import io.feling.loginwithgoogle.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class SignOut implements PojoRequestHandler<APIGatewayRequest, APIGatewayResponse> {


    @Override
    public APIGatewayResponse handleRequest(APIGatewayRequest input, Context context) {
        try {
            if(StringUtils.isNotEmpty(input.getQueryParameters().get("doNotDie"))) {
                return new APIGatewayResponse(new Response().msg("alive").toString());
            }

            MongoClients.getCollection("userInfo", UserInfo.class)
                    .findOneAndUpdate(
                            and(
                                    eq("isDel", 0),
                                    eq("accessToken", input.getQueryParameters().get("accessToken"))
                            ),
                            combine(
                                    set("accessTokenExpiredAt", new Date()),
                                    currentDate("lastModified")
                            )
                    );
            return new APIGatewayResponse(new Response().toString());
        } catch (Exception e) {
            return new APIGatewayResponse(new Response().fail().errCode("E000001").msg(e.getMessage()).toString());
        }
    }

}
