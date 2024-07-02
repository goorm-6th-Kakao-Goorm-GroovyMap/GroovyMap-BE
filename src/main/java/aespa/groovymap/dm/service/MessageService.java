package aespa.groovymap.dm.service;

import aespa.groovymap.dm.dto.MessageDto;
import aespa.groovymap.dm.repository.MessageRepository;
import aespa.groovymap.dm.repository.MessageRoomRepository;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.Message;
import aespa.groovymap.domain.MessageRoom;
import aespa.groovymap.repository.MemberRepository;
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

        // 메시지 방에 메시지 추가 및 저장
        messageRoom.getMessages().add(message);
        messageRoomRepository.save(messageRoom);

        return convertToDto(message);
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
        List<Message> messages = messageRepository.findMessagesByMessageRoomIdOrderedByTimestampDesc(messageRoomId);

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
}
