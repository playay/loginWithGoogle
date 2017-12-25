package io.feling.loginwithgoogle.api;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.PojoRequestHandler;
import io.feling.loginwithgoogle.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static com.mongodb.client.model.Filters.*;

@Slf4j
public class GetLoginInfo implements PojoRequestHandler<APIGatewayRequest, APIGatewayResponse> {


    @Override
    public APIGatewayResponse handleRequest(APIGatewayRequest input, Context context) {
        try {
            UserInfo userInfo = MongoClients.getCollection("userInfo", UserInfo.class)
                    .find(
                            and(
                                    eq("isDel", 0),
                                    eq("accessToken", input.getQueryParameters().get("accessToken")),
                                    gt("accessTokenExpiredAt", new Date())
                            )
                    ).first();
            return new APIGatewayResponse(new Response().data(userInfo).toString());
        } catch (Exception e) {
            return new APIGatewayResponse(new Response().fail().errCode("E000001").msg(e.getMessage()).toString());
        }
    }

}
