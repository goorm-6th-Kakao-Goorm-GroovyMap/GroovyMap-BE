package aespa.groovymap.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private Long id;
    private Long memberId;
    private String nickname;
    private String region;
    private String part;
    private String introduction;
    private String profileImage;
}
