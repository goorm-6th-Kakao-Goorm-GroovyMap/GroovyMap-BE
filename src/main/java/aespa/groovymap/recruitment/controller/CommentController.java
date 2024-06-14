package aespa.groovymap.recruitment.controller;

import aespa.groovymap.recruitment.dto.CommentRequestDto;
import aespa.groovymap.recruitment.dto.CommentResponseDto;
import aespa.groovymap.recruitment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recruitboard")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 추가", description = "새 댓글을 추가합니다.")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto) {
        try {
            log.debug("Adding comment with postId: " + postId + ", authorId: " + commentRequestDto.getAuthorId());
            commentRequestDto.setPostId(postId);
            CommentResponseDto responseDto = commentService.addComment(commentRequestDto);
            log.info("댓글 추가 성공");
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("댓글 추가 실패", e);
            return new ResponseEntity<>("댓글 추가 실패", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "댓글 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable Long postId) {
        try {
            List<CommentResponseDto> responseDtos = commentService.getCommentsByPostId(postId);
            log.info("댓글 조회 성공");
            return ResponseEntity.ok().body(responseDtos);
        } catch (Exception e) {
            log.error("댓글 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 조회 실패");
        }
    }
}
