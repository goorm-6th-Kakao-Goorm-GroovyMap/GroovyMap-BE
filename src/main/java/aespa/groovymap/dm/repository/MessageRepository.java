package aespa.groovymap.dm.repository;

import aespa.groovymap.domain.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 특정 메시지 방의 메시지를 시간 역순으로 조회하는 쿼리
    @Query("SELECT m FROM Message m WHERE m.messageRoom.id = :messageRoomId ORDER BY m.timestamp DESC")
    List<Message> findMessagesByMessageRoomIdOrderedByTimestampDesc(Long messageRoomId);

    // 특정 메시지 방에서 수신자가 읽지 않은 메시지를 읽음 처리하는 쿼리
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.messageRoom.id = :messageRoomId AND m.isRead = false AND m.receiver.id = :receiverId")
    void markMessagesAsRead(@Param("messageRoomId") Long messageRoomId, @Param("receiverId") Long receiverId);
}
