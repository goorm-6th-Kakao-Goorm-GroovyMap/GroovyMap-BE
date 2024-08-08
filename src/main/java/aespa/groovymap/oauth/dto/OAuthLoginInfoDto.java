package aespa.groovymap.oauth.dto;

import lombok.Data;

@Data
public class OAuthLoginInfoDto {

    private String nickname;
    private String email;

    public OAuthLoginInfoDto(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
