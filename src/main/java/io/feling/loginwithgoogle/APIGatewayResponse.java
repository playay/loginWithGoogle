package io.feling.loginwithgoogle;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIGatewayResponse implements Serializable {

    private static final long serialVersionUID = 636533117518516794L;

    private boolean isBase64Encoded;

    private int statusCode;

    private Map<String, String> headers;

    private String body;

    public APIGatewayResponse(String body) {
        isBase64Encoded = false;
        statusCode = 200;
        headers = Maps.newHashMap();
        this.body = body;
    }
}
