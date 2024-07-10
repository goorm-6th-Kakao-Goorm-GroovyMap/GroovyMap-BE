package aespa.groovymap.recruitment.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.recruitment.dto.MyListDto;
import aespa.groovymap.recruitment.dto.RecruitTeamMemberRequestDto;
import aespa.groovymap.recruitment.dto.RecruitTeamMemberResponseDto;
import aespa.groovymap.recruitment.service.RecruitTeamMemberService;
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
@RequestMapping("/recruitboard")
public class RecruitTeamMemberController {

    private final RecruitTeamMemberService recruitTeamMemberService;

    // 전체 팀원 모집 목록 조회
    @Operation(summary = "팀원 모집 목록 요청", description = "팀원 모집 목록을 요청합니다.")
    @GetMapping
    public ResponseEntity<?> getRecruitTeamMemberPost() {
        try {
            // 서비스 레이어를 호출하여 모든 팀원 모집 목록 조회
            List<RecruitTeamMemberRequestDto> recruitTeamMemberRequestDtos = recruitTeamMemberService.findAll();

            // 조회된 팀원 모집 목록을 HTTP 200 OK 상태와 함께 응답 본문으로 반환
            log.info("팀원 모집 목록 조회 성공");
            return ResponseEntity.ok().body(recruitTeamMemberRequestDtos);
        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            log.error("팀원 모집 목록 조회 실패", e);

            // 오류 메시지와 함께 HTTP 500 Internal Server Error 상태로 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("팀원 모집 목록 조회 실패");
        }
    }

    // 팀원 모집 글쓰기
    @Operation(summary = "팀원 모집 게시글 작성", description = "새로운 팀원 모집 목록 작성합니다.")
    @PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> writeRecruitTeamMember(@ModelAttribute RecruitTeamMemberResponseDto recruitTeamMemberResponseDto,
                                                    @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        try {
            RecruitTeamMemberRequestDto requestDto = recruitTeamMemberService.createRecruitTeamMember(recruitTeamMemberResponseDto);
            log.info("팀원 모집 게시글 등록 성공");
            return new ResponseEntity<>(requestDto, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("팀원 모집 게시글 등록 실패", e);
            return new ResponseEntity<>("팀원 모집 게시글 등록 실패", HttpStatus.BAD_REQUEST);
        }
    }

    // 팀원 모집 게시글 단건 조회
    @Operation(summary = "팀원 모집 게시글 조회", description = "특정 팀원 모집 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<?> getRecruitTeamMemberPost(@PathVariable Long postId) {
        try {
            RecruitTeamMemberRequestDto recruitTeamMemberRequestDto = recruitTeamMemberService.readOne(postId);
            log.info("팀원 모집 게시글 조회 성공");
            return ResponseEntity.ok().body(recruitTeamMemberRequestDto);
        } catch (Exception e) {
            log.error("팀원 모집 게시글 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("팀원 모집 게시글 조회 실패");
        }
    }

    // 팀원 모집 게시글 저장 요청
    @Operation(summary = "팀원 모집 게시글 저장 요청", description = "팀원 모집 게시글을 저장 요청합니다.")
    @PostMapping("/{postId}/save")
    public ResponseEntity<?> savePost(@PathVariable Long postId,
                                      @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {

        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            recruitTeamMemberService.savePost(postId, memberId);
            log.info("자유 게시판 게시글 저장 성공: postId = {}", postId);
            return ResponseEntity.ok().body("자유 게시판 게시글 저장 성공");
        } catch (Exception e) {
            log.error("자유 게시판 게시글 저장 실패: postId = {}, error ={}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 팀원 모집 게시글 좋아요 요청
    @Operation(summary = "팀원 모집 게시글 좋아요 요청", description = "팀원 모집 게시글에 좋아요를 요청합니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId,
                                      @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            recruitTeamMemberService.likePost(postId, memberId);
            log.info("자유 게시판 게시글 좋아요 성공: postId = {}", postId);
            return ResponseEntity.ok().body("자유 게시판 게시글 좋아요 성공");
        } catch (Exception e) {
            log.error("자유 게시판 게시글 좋아요 실패: postId = {}, error = {}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 팀원 모집 게시글 삭제 요청
    @Operation(summary = "팀원 모집 게시글 삭제 요청", description = "팀원 모집 게시글을 삭제 요청합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.info("로그인이 팔요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            recruitTeamMemberService.deletePost(postId, memberId);
            log.info("팀원 모집 게시글 삭제 성공: postId = {}, postId");
            return ResponseEntity.ok().body("팀원 모집 게시글 삭제 성공");
        } catch (Exception e) {
            log.error("팀원 모집 게시글 삭제 실패: postId = {}, error ={}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 로그인한 사용자가 좋아요, 저장한 팀원 모집 게시글 목록 조회
    @Operation(summary = "사용자가 좋아요, 저장한 팀원 모집 게시글 목록 조회", description = "사용자가 좋아요, 저장한 팀원 모집 게시글 목록을 조회합니다.")
    @GetMapping("/myList")
    public ResponseEntity<?> getMyList(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            MyListDto myListDto = recruitTeamMemberService.getMyList(memberId);
            log.info("사용자가 좋아요, 저장한 팀원 모집 게시글 목록 조회 성공");
            return ResponseEntity.ok().body(myListDto);
        } catch (Exception e) {
            log.error("사용자가 좋아요, 저장한 팀원 모집 게시글 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자가 좋아요, 저장한 팀원 모집 게시글 목록 조회 실패");
        }
    }
}