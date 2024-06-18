package aespa.groovymap.email.service;

import aespa.groovymap.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    private static final String FROM_EMAIL = "choijun0627@gmail.com";
    private static final String EMAIL_SUBJECT = "GroovyMap 회원 가입 인증 번호";
    private static final long CERTIFICATION_CODE_EXPIRE_TIME = 60 * 3L; // 3분

    private String createCertificationCode() {
        int certificationCodeLength = 6;
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int length = 0; length < certificationCodeLength; length++) {
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }

    private MimeMessage createMimeMessage(String email, String certificationCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        String emailContent = "<h3>요청하신 인증 번호입니다.</h3>"
                + "<h1>" + certificationCode + "</h1>"
                + "<h3>감사합니다.</h3>";

        message.setFrom(FROM_EMAIL);
        message.setRecipients(RecipientType.TO, email);
        message.setSubject(EMAIL_SUBJECT);
        message.setText(emailContent, "UTF-8", "html");

        return message;
    }

    @Async
    public void sendEmail(String email) {
        String certificationCode = createCertificationCode();
        try {
            MimeMessage message = createMimeMessage(email, certificationCode);
            javaMailSender.send(message);
            // Redis에 해당 인증 코드와 이메일을 인증 시간 3분으로 설정
            redisUtil.setDataExpire(certificationCode, email, CERTIFICATION_CODE_EXPIRE_TIME);
        } catch (MessagingException e) {
            log.error("이메일 전송 관련 예외 발생. 수신 이메일: {}, 예외: {}", email, e);
            throw new RuntimeException("이메일 전송 중 문제가 발생했습니다.");
        }
    }

    public Boolean checkCertificationCode(String email, String certificationCode) {
        String storedEmail = redisUtil.getData(certificationCode);
        return email.equals(storedEmail);
    }
}