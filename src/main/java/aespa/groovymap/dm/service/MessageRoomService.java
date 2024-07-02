package aespa.groovymap.dm.service;

import aespa.groovymap.dm.dto.MessageRoomDto;
import aespa.groovymap.dm.repository.MessageRoomRepository;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.Message;
import aespa.groovymap.domain.MessageRoom;
import aespa.groovymap.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;

    // 특정 회원이 속한 모든 메시지 방을 조회하여 DTO 리스트로 반환하는 메서드
    public List<MessageRoomDto> getMessageRooms(Long memberId) {
        // 주어진 회원 ID를 기반으로 해당 회원이 참여하고 있는 모든 메시지 방을 조회
        List<MessageRoom> messageRooms = messageRoomRepository.findAllByParticipant(memberId);
        // MessageRoomDto 객체들을 저장할 리스트를 초기화
        List<MessageRoomDto> messageRoomDtos = new ArrayList<>();

        // 조회된 각 메시지 방에 대해 MessageRoomDto 객체를 생성하여 리스트에 추가 반복
        for (MessageRoom messageRoom : messageRooms) {
            messageRoomDtos.add(createMessageRoomDto(messageRoom, memberId));
        }

        log.info("메시지 방 조회 성공 - memberId: {}", memberId);
        return messageRoomDtos;
    }

    // 메시지 방 정보를 바탕으로 MessageRoomDto를 생성하는 메서드
    private MessageRoomDto createMessageRoomDto(MessageRoom messageRoom, Long memberId) {
        // 메시지 방의 마지막 메시지를 가져옴
        Message lastMessageObj = messageRoom.getMessages().isEmpty() ? null
                : messageRoom.getMessages().get(messageRoom.getMessages().size() - 1);
        // 마지막 메시지의 내용과 시간 추출
        String lastMessage = lastMessageObj == null ? "" : lastMessageObj.getContents();
        String lastMessageTime = lastMessageObj == null ? "" : lastMessageObj.getTimestamp().toString();

        // 상대방의 ID를 결정
        Long receiverId = messageRoom.getReceiver().getId();
        Long senderId = messageRoom.getSender().getId();
        Long otherUserId = receiverId.equals(memberId) ? senderId : receiverId;
        // 상대방의 프로필 이미지 URL을 가져옴
        String otherUserProfileImage = getProfileImageByMemberId(otherUserId);

        // MessageRoomDto 객체를 생성하여 반환
        return MessageRoomDto.builder()
                .id(messageRoom.getId())
                .myId(memberId)
                .myNickname(getNicknameByMemberId(memberId))
                .otherUserId(otherUserId)
                .otherUserNickname(getNicknameByMemberId(otherUserId))
                .otherUserProfileImage(otherUserProfileImage)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .unreadCount(countUnreadMessagesByRoom(messageRoom.getId(), memberId))
                .build();
    }

    // 회원 ID로 회원의 닉네임을 조회하는 메서드
    private String getNicknameByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .map(Member::getNickname)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));
    }

    // 회원 ID로 회원의 프로필 이미지 URL을 조회하는 메서드
    private String getProfileImageByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .map(Member::getMemberContent)
                .map(MemberContent::getProfileImage)
                .orElse(null);
    }

    // 특정 메시지 방에서 사용자가 읽지 않은 메시지 개수를 조회하는 메서드
    public Long countUnreadMessagesByRoom(Long messageRoomId, Long userId) {
        if (!messageRoomRepository.existsById(messageRoomId)) {
            throw new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.");
        }
        return messageRoomRepository.countUnreadMessagesByMessageRoomIdAndReceiverId(messageRoomId, userId);
    }

    // 사용자가 모든 메시지 방에서 읽지 않은 메시지의 총 개수를 조회하는 메서드
    public Long countTotalUnreadMessages(Long memberId) {
        return messageRoomRepository.countTotalUnreadMessagesByMemberId(memberId);

    }


    public MessageRoom getMessageRoom(Long receiverId, Long senderId) {
        return messageRoomRepository.findByParticipants(senderId, receiverId)
                .stream()
                .findFirst()
                .orElseGet(() -> createNewMessageRoom(receiverId, senderId));
    }

    private MessageRoom createNewMessageRoom(Long receiverId, Long senderId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID: " + receiverId));
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID: " + senderId));

        MessageRoom messageRoom = MessageRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .messages(new ArrayList<>())
                .build();
        messageRoomRepository.save(messageRoom);
        log.info("새 메시지 방 생성 - senderId: {}, receiverId: {}", senderId, receiverId);
        return messageRoom;
    }
}
