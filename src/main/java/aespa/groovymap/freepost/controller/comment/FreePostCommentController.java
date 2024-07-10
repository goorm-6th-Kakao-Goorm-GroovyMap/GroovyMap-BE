package aespa.groovymap.freepost.controller.comment;

import aespa.groovymap.freepost.dto.comment.FreePostCommentRequestDto;
import aespa.groovymap.freepost.dto.comment.FreePostCommentResponseDto;
import aespa.groovymap.freepost.service.comment.FreePostCommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/freeboard")
public class FreePostCommentController {

    @Autowired
    private final FreePostCommentService freePostCommentService;

    @Operation(summary = "댓글 추가", description = "새 댓글을 추가합니다.")
    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody FreePostCommentRequestDto freePostCommentRequestDto) {
        try {
            log.debug("Adding comment with postId: " + postId + ", commentAuthorId: " + freePostCommentRequestDto.getCommentAuthor());
            freePostCommentRequestDto.setPostId(postId);
            FreePostCommentResponseDto freePostCommentResponseDto = freePostCommentService.addComment(freePostCommentRequestDto);
            log.info("댓글 추가 성공");
            return new ResponseEntity<>(freePostCommentResponseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("댓글 추가 실패", e);
            return new ResponseEntity<>("댓글 추가 실패", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "댓글 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    @GetMapping("/{postId}/comment")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable Long postId) {
        try {
            List<FreePostCommentResponseDto> responseDtos = freePostCommentService.getCommentsByPostId(postId);
            log.info("댓글 조회 성공");
            return ResponseEntity.ok().body(responseDtos);
        } catch (Exception e) {
            log.error("댓글 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 조회 실패");
        }
    }
}
