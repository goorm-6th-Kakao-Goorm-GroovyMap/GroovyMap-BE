package aespa.groovymap.register.controller;

import aespa.groovymap.register.dto.NicknameRequestDto;
import aespa.groovymap.register.dto.NicknameResponseDto;
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
    public ResponseEntity nicknameCheck(NicknameRequestDto nicknameRequestDto) {
        Boolean available = registerService.nicknameCheck(nicknameRequestDto.getNickname());
        NicknameResponseDto nicknameResponseDto = new NicknameResponseDto();
        nicknameResponseDto.setAvailable(available);
        return ResponseEntity.ok(nicknameResponseDto);
    }
}
