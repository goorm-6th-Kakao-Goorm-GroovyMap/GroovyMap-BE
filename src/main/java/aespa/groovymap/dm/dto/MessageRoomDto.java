package aespa.groovymap.dm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRoomDto {
    private Long id;
    private Long myId;
    private String myNickname;
    private Long otherUserId;
    private String otherUserNickname;
    private String otherUserProfileImage;
    private String lastMessage;
    private String lastMessageTime;
    private Long unreadCount;
}
