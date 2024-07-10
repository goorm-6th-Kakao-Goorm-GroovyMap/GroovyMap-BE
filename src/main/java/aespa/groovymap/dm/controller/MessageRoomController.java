package aespa.groovymap.dm.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.dm.dto.MessageDto;
import aespa.groovymap.dm.dto.MessageRoomDto;
import aespa.groovymap.dm.service.MessageRoomService;
import aespa.groovymap.dm.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/message-room")
public class MessageRoomController {

    private final MessageRoomService messageRoomService;
    private final MessageService messageService;

    @Operation(summary = "메시지 방 조회", description = "회원이 속한 모든 메시지 방을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getMessageRooms(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.error("로그인이 필요한 서비스입니다.");
            return ResponseEntity.badRequest().body("로그인이 필요한 서비스입니다.");
        }
        try {
            List<MessageRoomDto> messageRooms = messageRoomService.getMessageRooms(memberId);
            log.info("메시지 방 조회 성공 - memberId: {}", memberId);
            return ResponseEntity.ok(messageRooms);
        } catch (Exception e) {
            log.error("메시지 방 조회 실패 - memberId: {}", memberId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메시지 방 조회 실패");
        }
    }

    @Operation(summary = "메시지 조회", description = "메시지 방에 속한 모든 메시지를 조회합니다.")
    @GetMapping("/{messageRoomId}")
    public ResponseEntity<?> getMessages(@PathVariable Long messageRoomId,
                                         @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.error("로그인이 필요한 서비스입니다.");
            return ResponseEntity.badRequest().body("로그인이 필요한 서비스입니다.");
        }
        try {
            List<MessageDto> messages = messageService.getMessages(messageRoomId, memberId);
            messages.forEach(messageDto -> {
                if (messageDto.getReceiverId().equals(memberId)) {
                    messageDto.setRead(true);
                }
            });
            log.info("메시지 조회 성공: messageRoomId={}", messageRoomId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("메시지 조회 실패: messageRoomId={}", messageRoomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메시지 조회 실패");
        }
    }

    @Operation(summary = "안 읽은 메시지 개수 조회", description = "해당 메시지 방에서 안 읽은 메시지 개수를 조회합니다.")
    @GetMapping("/unread-count/{messageRoomId}")
    public ResponseEntity<?> countUnreadMessagesByRoom(@PathVariable Long messageRoomId,
                                                       @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.error("로그인이 필요한 서비스입니다.");
            return ResponseEntity.badRequest().body("로그인이 필요한 서비스입니다.");
        }
        Long count = messageRoomService.countUnreadMessagesByRoom(messageRoomId, memberId);
        log.info(messageRoomId + " 해당 채팅의 안 읽은 메시지 개수 조회 성공 count: " + count);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "전체 안 읽은 메시지 개수 조회", description = "전체 안 읽은 메시지 개수를 조회합니다.")
    @GetMapping("/unread-count")
    public ResponseEntity<?> countTotalUnreadMessages(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.error("로그인이 필요한 서비스입니다.");
            return ResponseEntity.badRequest().body("로그인이 필요한 서비스입니다.");
        }
        Long count = messageRoomService.countTotalUnreadMessages(memberId);
        log.info("전체 안 읽은 메시지 개수 조회 성공 member_id: " + memberId + " count: " + count);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "해당 채팅방의 마지막 메시지 조회", description = "해당 채팅방의 마지막 메시지를 조회합니다.")
    @GetMapping("/{messageRoomId}/last-message")
    public ResponseEntity<?> getLastMessage(@PathVariable Long messageRoomId) {
        try {
            String message = messageService.getLastMessage(messageRoomId);
            log.info("마지막 메시지 조회 성공: messageRoomId={}", messageRoomId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("마지막 메시지 조회 실패: messageRoomId={}", messageRoomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("마지막 메시지 조회 실패");
        }
    }
}
