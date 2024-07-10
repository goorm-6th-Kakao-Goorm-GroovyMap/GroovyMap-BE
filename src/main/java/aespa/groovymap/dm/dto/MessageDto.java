package aespa.groovymap.dm.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private boolean isRead;
    private boolean isSentByMe;
    private ZonedDateTime sendTime;
    private String otherUserProfileImage;
}
