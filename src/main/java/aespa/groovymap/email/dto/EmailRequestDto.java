package aespa.groovymap.email.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailRequestDto {
    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;
}
