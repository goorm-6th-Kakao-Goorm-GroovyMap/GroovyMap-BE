package aespa.groovymap.promotionpost.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.promotionpost.dto.PromotionPostRequestDto;
import aespa.groovymap.promotionpost.dto.PromotionPostResponseDto;
import aespa.groovymap.promotionpost.service.PromotionPostService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/promotionboard")
public class PromotionPostController {

    private final PromotionPostService promotionPostService;

    // 전체 홍보게시판 게시글 조회
    @Operation(summary = "모든 홍보 게시글 조회", description = "모든 홍보 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            // 서비스 레이어를 호출하여 모든 홍보 게시글을 조회
            List<PromotionPostRequestDto> promotionPostRequestDtos = promotionPostService.findAll();

            // 조회된 게시글 목록을 HTTP 200 OK 상태와 함께 응답 본문으로 반환
            log.info("홍보게시판 게시글 조회 성공");
            return ResponseEntity.ok().body(promotionPostRequestDtos);
        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            log.error("홍보게시판 게시글 조회 실패", e);

            // 오류 메시지와 함께 HTTP 500 Internal Server Error 상태로 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("홍보게시판 게시글 조회 실패");
        }
    }

    // 홍보게시판 게시글 등록
    @Operation(summary = "홍보 게시글 작성", description = "새로운 홍보 게시글을 작성합니다.")
    @PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> writePost(@ModelAttribute PromotionPostResponseDto promotionPostResponseDto,
                                       @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {

        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        try {
            PromotionPostRequestDto requestDto = promotionPostService.createPromotionPost(promotionPostResponseDto,
                    memberId);
            log.info("홍보게시판 게시글 등록 성공");
            return new ResponseEntity<>(requestDto, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            log.error("홍보게시판 게시글 등록 실패", e);
            return new ResponseEntity<>("홍보게시판 게시글 등록 실패", HttpStatus.BAD_REQUEST);
        }
    }

    // 홍보게시판 게시글 단건 조회
    @Operation(summary = "홍보 게시글 조회", description = "특정 홍보 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            PromotionPostRequestDto promotionPostRequestDto = promotionPostService.readOne(postId);
            log.info("홍보게시판 게시글 조회 성공");
            return ResponseEntity.ok().body(promotionPostRequestDto);
        } catch (Exception e) {
            log.error("홍보게시판 게시글 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("홍보게시판 게시글 조회 실패");
        }
    }

    // 홍보게시판 목록 분야별 정렬
    @Operation(summary = "홍보 게시글 분야별 조회", description = "특정 분야의 홍보 게시글을 조회합니다.")
    @GetMapping("/part/{part}")
    public ResponseEntity<?> getPostsByPart(@PathVariable String part) {
        try {
            List<PromotionPostRequestDto> promotionPostRequestDtos = promotionPostService.findByPart(part);
            log.info("홍보게시판 게시글 분야별 조회 성공: part = {}", part);
            return ResponseEntity.ok().body(promotionPostRequestDtos);
        } catch (Exception e) {
            log.error("홍보게시판 게시글 분야별 조회 실패: part = {}, error = {}", part, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 홍보 게시판 글 저장 요청
    @Operation(summary = "홍보 게시글 저장 요청", description = "홍보 게시글을 저장 요청합니다.")
    @PostMapping("/{postId}/save")
    public ResponseEntity<?> savePost(@PathVariable Long postId,
                                      @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            promotionPostService.savePost(postId, memberId);
            log.info("홍보게시판 게시글 저장 성공: postId = {}", postId);
            return ResponseEntity.ok().body("홍보게시판 게시글 저장 성공");
        } catch (Exception e) {
            log.error("홍보게시판 게시글 저장 실패: postId = {}, error = {}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 홍보 게시판 글 좋아요 요청
    @Operation(summary = "홍보 게시글 좋아요 요청", description = "홍보 게시글에 좋아요를 요청합니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId,
                                      @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            promotionPostService.likePost(postId, memberId);
            log.info("홍보게시판 게시글 좋아요 성공: postId = {}", postId);
            return ResponseEntity.ok().body("홍보게시판 게시글 좋아요 성공");
        } catch (Exception e) {
            log.error("홍보게시판 게시글 좋아요 실패: postId = {}, error = {}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

}
