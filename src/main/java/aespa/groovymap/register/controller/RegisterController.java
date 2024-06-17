package aespa.groovymap.register.controller;

import aespa.groovymap.register.dto.AvailableDto;
import aespa.groovymap.register.dto.EmailDto;
import aespa.groovymap.register.dto.NicknameDto;
import aespa.groovymap.register.dto.RegisterRequestDto;
import aespa.groovymap.register.dto.RegisterResponseDto;
import aespa.groovymap.register.dto.User;
import aespa.groovymap.register.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register/nickname-check")
    public ResponseEntity<AvailableDto> nicknameCheck(@RequestBody NicknameDto nicknameDto) {
        return checkAvailability(registerService.nicknameCheck(nicknameDto.getNickname()));
    }

    @PostMapping("/register/email-check")
    public ResponseEntity<AvailableDto> emailCheck(@RequestBody EmailDto emailDto) {
        return checkAvailability(registerService.emailCheck(emailDto.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDto registerRequestDto) {
        User user = registerService.register(registerRequestDto);
        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setUser(user);
        if (user != null) {
            registerResponseDto.setMessage("회원가입에 성공했습니다!");
            registerResponseDto.setResult(true);
            return ResponseEntity.ok(registerResponseDto);
        } else {
            registerResponseDto.setMessage("회원가입에 실패했습니다!");
            registerResponseDto.setResult(false);
            return ResponseEntity.status(409).body(registerResponseDto);
        }
    }

    private ResponseEntity<AvailableDto> checkAvailability(Boolean available) {
        AvailableDto availableDto = new AvailableDto();
        availableDto.setAvailable(available);
        return ResponseEntity.ok(availableDto);
    }
}
