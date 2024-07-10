package aespa.groovymap.dm.repository;

import aespa.groovymap.domain.MessageRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    // 두 사용자가 참여하고 있는 메시지 방을 조회하는 쿼리
    @Query("SELECT mr FROM MessageRoom mr WHERE (mr.sender.id = :userId1 AND mr.receiver.id = :userId2) OR (mr.sender.id = :userId2 AND mr.receiver.id = :userId1)")
    List<MessageRoom> findByParticipants(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // 특정 사용자가 참여하고 있는 모든 메시지 방을 조회하는 쿼리
    @Query("SELECT mr FROM MessageRoom mr WHERE mr.sender.id = :userId OR mr.receiver.id = :userId")
    List<MessageRoom> findAllByParticipant(@Param("userId") Long userId);


    // 특정 메시지 방에서 특정 사용자가 읽지 않은 메시지 개수를 세는 쿼리
    @Query("SELECT COUNT(m) FROM Message m WHERE m.messageRoom.id = :messageRoomId AND m.isRead = false AND m.receiver.id = :userId")
    long countUnreadMessagesByMessageRoomIdAndReceiverId(@Param("messageRoomId") Long messageRoomId,
                                                         @Param("userId") Long userId);


    // 특정 사용자가 모든 메시지 방에서 읽지 않은 메시지의 총 개수를 세는 쿼리
    @Query("SELECT COUNT(m) FROM Message m JOIN m.messageRoom r WHERE (r.sender.id = :memberId OR r.receiver.id = :memberId) AND m.isRead = false AND m.receiver.id = :memberId")
    Long countTotalUnreadMessagesByMemberId(@Param("memberId") Long memberId);


}
