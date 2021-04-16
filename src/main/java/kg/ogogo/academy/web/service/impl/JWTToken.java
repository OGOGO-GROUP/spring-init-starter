package kg.ogogo.academy.web.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.ogogo.academy.web.dto.UserDTO;

import java.io.Serializable;

public class JWTToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accessToken;

    private UserDTO user;

    public JWTToken(String idToken, UserDTO user) {
        this.accessToken = idToken;
        this.user = user;
    }

    @JsonProperty("token")
    public String getAccessToken() {
        return this.accessToken;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
