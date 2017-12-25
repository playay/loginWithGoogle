package io.feling.loginwithgoogle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIGatewayRequest implements Serializable {

    private static final long serialVersionUID = 6646069294087305664L;

    /**
     * api request path
     */
    private String path;

    /**
     * request method name
     */
    private String httpMethod;

    /**
     * all headers,including system headers
     */
    private Map<String, String> headers;

    /**
     * query parameters
     */
    private Map<String, String> queryParameters;

    /**
     * path parameters
     */
    private Map<String, String> pathParameters;

    /**
     * string of request payload
     */
    private String body;

    /**
     * true|false, indicate if the body is Base64-encode
     */
    private boolean isBase64Encoded;


}