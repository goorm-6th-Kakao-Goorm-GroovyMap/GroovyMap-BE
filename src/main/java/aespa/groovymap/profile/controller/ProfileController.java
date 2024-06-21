package aespa.groovymap.profile.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.profile.dto.ProfileDto;
import aespa.groovymap.profile.service.ProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        } catch (Exception e) {
            log.error("프로필 추가 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 추가 실패");
        }

        log.info("프로필 추가 완료 : {}", memberId);
        return ResponseEntity.ok("프로필 추가 완료");
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
}
