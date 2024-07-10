package aespa.groovymap.email.controller;

import aespa.groovymap.email.dto.EmailCheckDto;
import aespa.groovymap.email.dto.EmailRequestDto;
import aespa.groovymap.email.dto.EmailResponseDto;
import aespa.groovymap.email.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;

    @PostMapping("/register/send-certification")
    public ResponseEntity sendEmail(@RequestBody EmailRequestDto emailRequestDto) {
        log.info("이메일 인증 요청");
        mailService.sendEmail(emailRequestDto.getEmail());
        return ResponseEntity.ok("email send complete");
    }

    @PostMapping("/register/certificate-code")
    public ResponseEntity checkCertificationCode(@RequestBody EmailCheckDto emailCheckDto) {
        log.info("이메일 인증 확인 요청");
        Boolean isValidCertificationCode = mailService.checkCertificationCode(emailCheckDto.getEmail(),
                emailCheckDto.getCertificationCode());
        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setResult(isValidCertificationCode);
        return ResponseEntity.ok(emailResponseDto); // 200 OK와 함께 true 반환
    }
}
