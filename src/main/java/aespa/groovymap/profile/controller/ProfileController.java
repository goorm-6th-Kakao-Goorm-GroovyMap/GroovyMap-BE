package aespa.groovymap.profile.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.profile.dto.ProfileDto;
import aespa.groovymap.profile.service.ProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/add")
    public ResponseEntity<?> createProfile(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {

        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            profileService.createProfile(memberId);
            log.info("프로필 추가 완료 : {}", memberId);
            return ResponseEntity.ok().body("프로필 추가 완료");
        } catch (IllegalArgumentException e) {
            // 이미 등록된 프로필이 있는 경우 또는 회원 정보가 없는 경우
            log.error("프로필 추가 실패 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("프로필 추가 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 추가 실패");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProfiles() {
        try {
            List<ProfileDto> profiles = profileService.getAllProfiles();
            log.info("모든 프로필 조회 완료");
            return ResponseEntity.ok().body(profiles);
        } catch (Exception e) {
            log.error("모든 프로필 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("모든 프로필 조회 실패");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {

        if (memberId == null) {
            log.info("로그인이 필요합니다.");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            profileService.deleteProfile(memberId);
            log.info("프로필 삭제 완료 : {}", memberId);
            return ResponseEntity.ok("프로필 삭제 완료");
        } catch (IllegalArgumentException e) {
            log.error("프로필 삭제 실패 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("프로필 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 삭제 실패");
        }
    }
}
