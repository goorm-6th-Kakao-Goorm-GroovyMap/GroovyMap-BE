package aespa.groovymap.oauth.dto;

import aespa.groovymap.oauth.util.LoginStatus;
import lombok.Data;

@Data
public class OAuthLoginResponseDto {
    private String email;
    private String nickname;
    private LoginStatus loginStatus;
}
