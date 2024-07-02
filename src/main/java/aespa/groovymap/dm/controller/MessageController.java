package aespa.groovymap.dm.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.dm.dto.MessageDto;
import aespa.groovymap.dm.dto.SendMessageRequestDto;
import aespa.groovymap.dm.service.MessageRoomService;
import aespa.groovymap.dm.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;
    private final MessageRoomService messageRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    // HTTP POST 요청 처리
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequestDto messageDto,
                                         @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.warn("로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            MessageDto savedMessage = messageService.saveMessage(memberId, messageDto.getReceiverId(),
                    messageDto.getContent());
            log.info("메시지 전송 성공: {}", savedMessage);
            return ResponseEntity.ok("메시지 전송 성공");
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메시지 전송 실패");
        }
    }

    @Operation(summary = "특정 사용자와의 메시지 목록 조회", description = "특정 사용자와의 메시지 목록을 조회합니다.")
    @GetMapping("/{receiverId}")
    public ResponseEntity<?> getMessagesByReceiverId(@PathVariable Long receiverId,
                                                     @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.error("로그인이 필요한 서비스입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }
        try {
            List<MessageDto> messages = messageService.getMessagesResponse(receiverId, memberId);
            log.info("메시지 조회 성공: receiverId={}, memberId={}", receiverId, memberId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            log.error("메시지 조회 실패: receiverId={}, memberId={}", receiverId, memberId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("메시지 조회 실패: receiverId={}, memberId={}", receiverId, memberId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메시지 조회 실패");
        }
    }

    @Operation(summary = "특정 사용자의 닉네임을 통한 메시지 목록 조회", description = "특정 사용자의 닉네임을 통한 메시지 목록을 조회합니다.")
    @GetMapping("/nickname")
    public ResponseEntity<?> getMessagesByNickname(@RequestParam String nickname,
                                                   @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.error("로그인이 필요한 서비스입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }
        if (nickname == null || nickname.isEmpty()) {
            log.error("닉네임이 제공되지 않았습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임이 제공되지 않았습니다.");
        }

        try {
            List<MessageDto> messages = messageService.getMessagesResponseByNickname(nickname, memberId);
            log.info("메시지 조회 성공: nickname={}, memberId={}", nickname, memberId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            log.error("사용자 조회 실패: nickname={}, memberId={}", nickname, memberId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("메시지 조회 실패: nickname={}, memberId={}", nickname, memberId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메시지 조회 실패");
        }
    }
}
