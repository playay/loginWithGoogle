package io.feling.loginwithgoogle.api;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import io.feling.loginwithgoogle.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

@Slf4j
public class GetLoginInfoInner implements PojoRequestHandler<APIGatewayRequest, APIGatewayResponse> {


    @Override
    public APIGatewayResponse handleRequest(APIGatewayRequest input, Context context) {
        try {
            if(StringUtils.isNotEmpty(input.getQueryParameters().get("doNotDie"))) {
                return new APIGatewayResponse(new Response().msg("alive").toString());
            }

            FindOneAndUpdateOptions option = new FindOneAndUpdateOptions();
            option.upsert(false);
            option.returnDocument(ReturnDocument.AFTER);

            UserInfo userInfo = MongoClients.getCollection("userInfo", UserInfo.class)
                    .findOneAndUpdate(
                            and(
                                    eq("isDel", 0),
                                    eq("accessToken", input.getQueryParameters().get("accessToken")),
                                    gt("accessTokenExpiredAt", new Date())
                            ),
                            combine(
                                    set("accessTokenExpiredAt", DateTime.now().plusDays(30).toDate()),
                                    currentDate("lastModified")
                            ),
                            option
                    );
            return new APIGatewayResponse(new Response().data(userInfo).toString());
        } catch (Exception e) {
            return new APIGatewayResponse(new Response().fail().errCode("E000001").msg(e.getMessage()).toString());
        }
    }

}
