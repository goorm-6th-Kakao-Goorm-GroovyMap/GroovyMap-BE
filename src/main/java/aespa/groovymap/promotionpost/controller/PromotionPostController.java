package aespa.groovymap.promotionpost.controller;

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
            List<PromotionPostResponseDto> promotionPostResponseDtos = promotionPostService.findAll();

            // 조회된 게시글 목록을 HTTP 200 OK 상태와 함께 응답 본문으로 반환
            log.info("홍보게시판 게시글 조회 성공");
            return ResponseEntity.ok().body(promotionPostResponseDtos);
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
    public ResponseEntity<?> writePost(@ModelAttribute PromotionPostRequestDto promotionPostRequestDto) {
        try {
            PromotionPostResponseDto responseDto = promotionPostService.createPromotionPost(promotionPostRequestDto);
            log.info("홍보게시판 게시글 등록 성공");
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // 201 Created
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
            PromotionPostResponseDto promotionPostResponseDto = promotionPostService.readOne(postId);
            log.info("홍보게시판 게시글 조회 성공");
            return ResponseEntity.ok().body(promotionPostResponseDto);
        } catch (Exception e) {
            log.error("홍보게시판 게시글 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("홍보게시판 게시글 조회 실패");
        }
    }


}
