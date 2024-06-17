package aespa.groovymap.register.dto;

import lombok.Data;

@Data
public class RegisterResponseDto {
    private User user;
    private String message;
    private Boolean result;
}
