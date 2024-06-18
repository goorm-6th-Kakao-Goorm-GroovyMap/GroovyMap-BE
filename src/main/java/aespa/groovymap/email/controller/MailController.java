package aespa.groovymap.email.controller;

import aespa.groovymap.email.dto.EmailCheckDto;
import aespa.groovymap.email.dto.EmailRequestDto;
import aespa.groovymap.email.dto.EmailResponseDto;
import aespa.groovymap.email.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/register/send-certification")
    public ResponseEntity sendEmail(@RequestBody EmailRequestDto emailRequestDto) {
        mailService.sendEmail(emailRequestDto.getEmail());
        return ResponseEntity.ok("email send complete");
    }

    @PostMapping("/register/certificate-code")
    public ResponseEntity checkCertificationCode(@RequestBody EmailCheckDto emailCheckDto) {
        Boolean isValidCertificationCode = mailService.checkCertificationCode(emailCheckDto.getEmail(),
                emailCheckDto.getCertificationCode());
        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setResult(isValidCertificationCode);
        return ResponseEntity.ok(emailResponseDto); // 200 OK와 함께 true 반환
    }
}
