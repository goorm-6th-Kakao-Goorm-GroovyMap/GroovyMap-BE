package aespa.groovymap.recruitment.dto;

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
    @NotNull(message = "Author ID는 null이 될 수 없습니다.")
    private Long authorId;
    private String content;
    private String date;
}
