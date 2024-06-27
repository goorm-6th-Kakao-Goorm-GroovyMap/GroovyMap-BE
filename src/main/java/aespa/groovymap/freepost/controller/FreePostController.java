package aespa.groovymap.freepost.controller;

import aespa.groovymap.freepost.dto.FreePostRequestDto;
import aespa.groovymap.freepost.dto.FreePostResponseDto;
import aespa.groovymap.freepost.service.FreePostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/freeboard")
public class FreePostController {

    private final FreePostService freePostService;

    // 전체 자유 게시판 게시글 조회
    @Operation(summary = "모든 자유 게시판 게시글 조회", description = "모든 자유 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            // 서비스 레이어를 호출하여 모든 자유 게시판 게시글을 조회
            List<FreePostRequestDto> freePostRequestDtos = freePostService.findAll();

            // 조회된 게시글 목록을 HTTP 200 OK 상태와 함께 응답 본문으로 반환
            log.info("자유 게시판 게시글 조회 성공");
            return ResponseEntity.ok().body(freePostRequestDtos);
        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            log.error("홍보게시판 게시글 조회 실패", e);

            // 오류 메시지와 함께 HTTP 500 Internal Server Error 상태로 응답
            return ResponseEntity.
                    status(HttpStatus.INTERNAL_SERVER_ERROR).body("자유 게시판 게시글 조회 실패");
        }
    }

    // 자유 게시판 게시글 등록
    @Operation(summary = "자유 게시판 게시글 작성", description = "새로운 자유 게시판 게시글을 작성합니다.")
    @PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> writePost(@ModelAttribute FreePostResponseDto freePostResponseDto) {
        try {
            FreePostRequestDto requestDto = freePostService.createFreePost(freePostResponseDto);
            log.info("자유 게시판 게시글 등록 성공");
            return new ResponseEntity<>(requestDto, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            log.error("자유 게시판 게시글 등록 실패", e);
            return new ResponseEntity<>("자유 게시판 게시글 등록 실패", HttpStatus.BAD_REQUEST);
        }
    }

    // 자유 게시판 게시글 단건 조회
    @Operation(summary = "자유 게시판 게시글 조회", description = "특정 자유 게시판 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            FreePostRequestDto freePostRequestDto = freePostService.readOne(postId);
            log.info("자유 게시판 게시글 조회 성공");
            return ResponseEntity.ok().body(freePostRequestDto);
        } catch (Exception e) {
            log.error("자유 게시판 게시글 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("자유 게시판 게시글 조회 실패");
        }
    }

/*
    // 자유 게시판 목록 분야별 정렬
    @Operation(summary = "자유 게시판 게시글 분야별 조회", description = "특정 분야의 자유 게시판 게시글을 조회합니다.")
    @GetMapping("/part/{part}")
    public ResponseEntity<?> getPostsByPart(@PathVariable String part) {
        try {
            List<FreePostRequestDto> freePostRequestDtos = freePostService.findByPart(part);
            log.info("자유 게시판 게시글 분야별 조회 성공: part = {}", part);
            return ResponseEntity.ok().body(freePostRequestDtos);
        } catch (Exception e) {
            log.error("자유 게시판 게시글 분야별 조회 실패: part = {}, error = {}", part, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
*/


}
