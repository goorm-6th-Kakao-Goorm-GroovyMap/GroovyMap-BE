package aespa.groovymap.register.controller;

import aespa.groovymap.register.dto.AvailableDto;
import aespa.groovymap.register.dto.EmailDto;
import aespa.groovymap.register.dto.NicknameDto;
import aespa.groovymap.register.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register/nickname-check")
    public ResponseEntity nicknameCheck(NicknameDto nicknameDto) {
        Boolean available = registerService.nicknameCheck(nicknameDto.getNickname());
        return ResponseEntity.ok(makeAvailableDto(available));
    }

    @PostMapping("/register/email-check")
    public ResponseEntity emailCheck(EmailDto emailDto) {
        Boolean available = registerService.emailCheck(emailDto.getEmail());
        return ResponseEntity.ok(makeAvailableDto(available));
    }

    public AvailableDto makeAvailableDto(Boolean available) {
        AvailableDto availableDto = new AvailableDto();
        availableDto.setAvailable(available);
        return availableDto;
    }
}
