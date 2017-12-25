package io.feling.loginwithgoogle;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * https://developers.google.com/identity/one-tap/web/idtoken-auth
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo implements Serializable {
    private static final long serialVersionUID = -5622179342994675394L;

    // These six fields are included in all Google ID Tokens.
    private String iss;
    private String sub;
    private String azp;
    private String aud;
    private String iat;
    private String exp;

    // These seven fields are only included when the user has granted the "profile" and "email" OAuth scopes to the application.

    private String email;
    @SerializedName("email_verified")
    private String emailVerified;
    private String name;
    private String picture;
    @SerializedName("given_name")
    private String givenName;
    @SerializedName("family_name")
    private String familyName;
}
