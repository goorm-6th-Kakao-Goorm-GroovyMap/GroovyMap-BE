package aespa.groovymap.email.service;

import aespa.groovymap.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private static String certificationCode;

    private void createCertificationCode() {
        int certificationCodeLength = 6;

        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int length = 0; length < certificationCodeLength; length++) {
            builder.append(random.nextInt(10));
        }

        certificationCode = builder.toString();
    }

    public MimeMessage createMail(String email) {
        createCertificationCode();

        MimeMessage message = javaMailSender.createMimeMessage();

        String fromMail = "choijun0627@gmail.com";
        String toMail = email;
        String emailTitle = "GroovyMap 회원 가입 인증 번호";
        String emailContent = "";
        emailContent += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
        emailContent += "<h1>" + certificationCode + "</h1>";
        emailContent += "<h3>" + "감사합니다." + "</h3>";

        try {
            message.setFrom(fromMail);
            message.setRecipients(RecipientType.TO, toMail);
            message.setSubject(emailTitle);
            message.setText(emailContent, "UTF-8", "html");
        } catch (MessagingException messagingException) {
            log.error("이메일 전송 관련 예외 발생. 수신 이메일: {}, 예외: {}", toMail, messagingException);
        }

        // Redis에 해당 인증 코드 인증 시간 3분으로 설정
        redisUtil.setDataExpire(certificationCode, toMail, 60 * 3L);

        return message;
    }

    public void sendEmail(String email) {
        MimeMessage message = createMail(email);
        javaMailSender.send(message);
    }

    public Boolean checkCertificationCode(String email, String certificationCode) {
        System.out.println(redisUtil.getData(certificationCode));
        System.out.println(email);
        if (redisUtil.getData(certificationCode) == null) {
            return false;
        } else if (redisUtil.getData(certificationCode).equals(email)) {
            return true;
        } else {
            return false;
        }
    }
}