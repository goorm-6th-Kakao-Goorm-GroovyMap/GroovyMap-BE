package aespa.groovymap.dm.service;

import aespa.groovymap.dm.dto.MessageDto;
import aespa.groovymap.dm.dto.SendMessageRequestDto;
import aespa.groovymap.dm.repository.MessageRepository;
import aespa.groovymap.dm.repository.MessageRoomRepository;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.Message;
import aespa.groovymap.domain.MessageRoom;
import aespa.groovymap.repository.MemberRepository;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageDto processAndSendMessage(SendMessageRequestDto messageDto, Principal principal) {
        try {
            // 사용자 인증 및 ID 획득
            Long memberId = authenticateAndGetMemberId(principal);

            // 수신자 ID가 발신자 ID와 같으면 예외 발생
            if (memberId.equals(messageDto.getReceiverId())) {
                log.warn("자신에게 메시지를 보낼 수 없습니다: 발신자 ID = {}, 수신자 ID = {}", memberId, messageDto.getReceiverId());
                throw new IllegalArgumentException("자신에게 메시지를 보낼 수 없습니다.");
            }

            // 메시지 저장
            MessageDto savedMessage = saveMessage(memberId, messageDto.getReceiverId(), messageDto.getContent());
            log.info(savedMessage.toString());

            // 수신자에게 메시지 전송
            sendMessageToUser(messageDto.getReceiverId(), savedMessage);

            savedMessage.setSentByMe(true);
            // 발신자에게 메시지 전송
            sendMessageToUser(memberId, savedMessage);

            // 성공 로그 기록
            log.info("메시지 전송 성공: 발신자 ID = {}, 수신자 ID = {}", memberId, messageDto.getReceiverId());

            return savedMessage;
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("메시지 전송 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("메시지 처리 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("메시지 처리 중 오류가 발생했습니다.", e);
        }
    }

    private Long authenticateAndGetMemberId(Principal principal) {
        if (principal == null) {
            log.error("인증되지 않은 사용자의 메시지 전송 시도");
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        return Long.parseLong(principal.getName());
    }

    private void sendMessageToUser(Long userId, MessageDto message) {
        try {
            messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/messages", message);
            log.debug("사용자에게 메시지 전송 완료: 사용자 ID = {}", userId);
        } catch (Exception e) {
            log.error("메시지 전송 실패: 사용자 ID = {}", userId, e);
            throw new RuntimeException("메시지 전송에 실패했습니다.", e);
        }
    }


    // 메시지 저장 로직
    public MessageDto saveMessage(Long senderId, Long receiverId, String content) {
        // 발신자 및 수신자 확인
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID: " + senderId));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID: " + receiverId));

        // 메시지 방이 없으면 새로 생성
        MessageRoom messageRoom = messageRoomRepository.findByParticipants(sender.getId(), receiver.getId())
                .stream()
                .findFirst()
                .orElseGet(() -> createNewMessageRoom(sender, receiver));

        // 메시지 생성
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .contents(content)
                .messageRoom(messageRoom)
                .isRead(false)
                .timestamp(ZonedDateTime.now())
                .build();

        // 메시지를 저장하고 저장된 엔티티를 반환받음
        Message savedMessage = messageRepository.save(message);

        // 메시지 방에 메시지 추가
        messageRoom.getMessages().add(savedMessage);
        messageRoomRepository.save(messageRoom);

        return convertToDto(savedMessage);
    }

    // 새로운 메시지 방 생성 메서드
    private MessageRoom createNewMessageRoom(Member sender, Member receiver) {
        MessageRoom messageRoom = MessageRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .messages(new ArrayList<>())
                .build();
        messageRoomRepository.save(messageRoom);
        return messageRoom;
    }

    // 메시지 목록 조회 응답 처리 메서드 (receiverId 기반)
    public List<MessageDto> getMessagesResponse(Long receiverId, Long memberId) {
        return fetchMessages(receiverId, memberId);
    }

    // 메시지 목록 조회 응답 처리 메서드 (닉네임 기반)
    public List<MessageDto> getMessagesResponseByNickname(String nickname, Long memberId) {
        Long receiverId = getMemberIdByNickname(nickname);
        return fetchMessages(receiverId, memberId);
    }

    // 메시지 목록 조회 내부 메서드
    private List<MessageDto> fetchMessages(Long receiverId, Long memberId) {
        // 메시지 방 조회
        MessageRoom messageRoom = getMessageRoom(receiverId, memberId);
        // 메시지 목록 조회
        List<MessageDto> messages = getMessages(messageRoom.getId(), memberId);
        // 메시지 읽음 처리
        messages.forEach(messageDto -> {
            if (messageDto.getReceiverId().equals(memberId)) {
                messageDto.setRead(true);
            }
        });
        return messages;
    }

    // 메시지 목록 조회 메서드
    public List<MessageDto> getMessages(Long messageRoomId, Long memberId) {
        // 메시지 방에 속한 메시지들을 시간 순으로 조회
        List<Message> messages = messageRepository.findMessagesByMessageRoomIdOrderedByTimestampAsc(messageRoomId);

        // 현재 사용자가 메시지의 수신자인 경우에만 메시지를 읽음 처리
        messageRepository.markMessagesAsRead(messageRoomId, memberId);

        // 메시지들을 MessageDto로 변환하여 반환
        return messages.stream()
                .map(message -> {
                    MessageDto messageDto = convertToDto(message);
                    boolean isSentByMe = message.getSender().getId().equals(memberId);
                    messageDto.setSentByMe(isSentByMe);

                    // 상대방의 프로필 이미지 URL을 가져옴
                    if (!isSentByMe) {
                        Long otherUserId = message.getSender().getId();
                        String otherUserProfileImage = memberRepository.findById(otherUserId)
                                .map(Member::getMemberContent)
                                .map(MemberContent::getProfileImage)
                                .orElse(null);
                        messageDto.setOtherUserProfileImage(otherUserProfileImage);
                    }
                    return messageDto;
                })
                .toList();
    }


    // Message 엔티티를 MessageDto로 변환
    private MessageDto convertToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .roomId(message.getMessageRoom().getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(message.getContents())
                .sendTime(message.getTimestamp())
                .isRead(message.isRead())
                .build();
    }

    // 해당 채팅방의 마지막 메시지 조회
    public String getLastMessage(Long messageRoomId) {
        List<Message> messages = messageRepository.findMessagesByMessageRoomIdOrderedByTimestampDesc(messageRoomId);
        return messages.isEmpty() ? "" : convertToDto(messages.get(0)).getContent();
    }

    // 사용자 닉네임을 통한 수신자 ID 조회
    public Long getMemberIdByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .map(Member::getId)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임을 가진 사용자가 없습니다."));
    }

    // 메시지 방 조회 메서드
    private MessageRoom getMessageRoom(Long receiverId, Long senderId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID: " + senderId));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID: " + receiverId));
        return messageRoomRepository.findByParticipants(senderId, receiverId)
                .stream()
                .findFirst()
                .orElseGet(() -> createNewMessageRoom(sender, receiver));
    }

    // 메시지 읽음 처리 메서드
    public void markMessageAsRead(Long messageId, Long receiverId) {
        // 수신자가 같은 경우만 읽음 처리
        if (messageRepository.findById(messageId)
                .map(Message::getReceiver)
                .map(Member::getId)
                .filter(receiverId::equals)
                .isPresent()) {
            messageRepository.markMessageAsRead(messageId);
            log.info("메시지 읽음 처리 성공: 메시지 ID = {}, 수신자 ID = {}", messageId, receiverId);
        }
    }
}
