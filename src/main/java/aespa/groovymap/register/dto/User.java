package aespa.groovymap.register.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Type;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String nickname;
    private String region;
    private Category part;
    private Type subPart;
}
