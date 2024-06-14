package aespa.groovymap.recruitment.service;

import aespa.groovymap.domain.Comment;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.post.RecruitTeamMemberPost;
import aespa.groovymap.recruitment.dto.CommentRequestDto;
import aespa.groovymap.recruitment.dto.CommentResponseDto;
import aespa.groovymap.recruitment.repository.CommentRepository;
import aespa.groovymap.recruitment.repository.MemberRepository;
import aespa.groovymap.recruitment.repository.RecruitTeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecruitTeamMemberRepository recruitTeamMemberRepository;
    private final MemberRepository memberRepository; // 추가

    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        if (commentRequestDto.getPostId() == null || commentRequestDto.getAuthorId() == null) {
            throw new IllegalArgumentException("Post ID와 Author ID는 null이 될 수 없습니다.");
        }

        RecruitTeamMemberPost post = recruitTeamMemberRepository.findById(commentRequestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 post ID입니다."));

        Member author = memberRepository.findById(commentRequestDto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 author ID입니다."));

        Comment comment = Comment.builder()
                .commentPost(post)
                .content(commentRequestDto.getContent())
                .commentAuthor(author)
                .timestamp(ZonedDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return convertToResponseDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private CommentResponseDto convertToResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getCommentPost().getId())
                .content(comment.getContent())
                .author(comment.getCommentAuthor().getNickname()) // Member의 이름이나 다른 식별자를 사용
                .timestamp(comment.getTimestamp())
                .build();
    }
}
