package aespa.groovymap.recruitment.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CommentRequestDto {
    @NotNull(message = "Post ID는 null이 될 수 없습니다.")
    private Long postId;
    @NotNull(message = "commentAuthor nickname은 null이 될 수 없습니다.")
    private String commentAuthor;
    private String content;
    private String date;
}
