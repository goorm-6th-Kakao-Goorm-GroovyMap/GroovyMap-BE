package aespa.groovymap.freepost.service.comment;

import aespa.groovymap.domain.Comment;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.post.FreePost;
import aespa.groovymap.freepost.dto.comment.FreePostCommentRequestDto;
import aespa.groovymap.freepost.dto.comment.FreePostCommentResponseDto;
import aespa.groovymap.freepost.repository.FreePostRepository;
import aespa.groovymap.repository.CommentRepository;
import aespa.groovymap.repository.MemberRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FreePostCommentService {

    private final CommentRepository commentRepository;
    private final FreePostRepository freePostRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FreePostCommentResponseDto addComment(FreePostCommentRequestDto freePostCommentRequestDto) {
        if (freePostCommentRequestDto.getPostId() == null || freePostCommentRequestDto.getCommentAuthor() == null) {
            throw new IllegalArgumentException(
                    "Post ID와 Author ID는 null이 될 수 없습니다. postId: " + freePostCommentRequestDto.getPostId() + ", authorId: "
                            + freePostCommentRequestDto.getCommentAuthor());
        }

        FreePost post = freePostRepository.findById(freePostCommentRequestDto.getPostId())
                .orElseThrow(
                        () -> new IllegalArgumentException("잘못된 post ID입니다. postId: " + freePostCommentRequestDto.getPostId()));

        Member author = memberRepository.findByNickname(freePostCommentRequestDto.getCommentAuthor())
                .orElseThrow(() -> new IllegalArgumentException(
                        "잘못된 author ID입니다. authorId: " + freePostCommentRequestDto.getCommentAuthor()));

        Comment comment = Comment.builder()
                .commentPost(post)
                .content(freePostCommentRequestDto.getContent())
                .commentAuthor(author)
                .timestamp(ZonedDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        post.getComments().add(savedComment);

        return convertToResponseDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<FreePostCommentResponseDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private FreePostCommentResponseDto convertToResponseDto(Comment comment) {
        return FreePostCommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getCommentPost().getId())
                .content(comment.getContent())
                .author(comment.getCommentAuthor().getNickname()) // Member의 이름이나 다른 식별자를 사용
                .timestamp(comment.getTimestamp())
                .build();
    }
}
