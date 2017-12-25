package io.feling.loginwithgoogle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 3600650832832803955L;

    private String userId;

    private String userEmail;
    private Boolean userEmailVerified;
    private String userName;
    private String givenName;
    private String familyName;
    private String userAvatar;

    private String accessToken;
    private Date accessTokenExpiredAt;

    private Date lastModified;
    private Date firstCreated;
    private Integer isDel;

}
