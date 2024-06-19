package aespa.groovymap.profile.dto;

import aespa.groovymap.domain.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private Long id;
    private Long memberId;
    private String nickname;
    private String region;
    private Category part;
    private String introduction;
    private String profileImage;
}
