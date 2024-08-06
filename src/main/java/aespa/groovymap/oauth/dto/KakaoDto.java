package aespa.groovymap.oauth.dto;

import lombok.Data;

@Data
public class KakaoDto {

    private String nickname;
    private String email;

    public KakaoDto(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
