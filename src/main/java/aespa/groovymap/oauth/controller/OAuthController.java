package aespa.groovymap.oauth.controller;

import static aespa.groovymap.oauth.util.OAuthType.GOOGLE;
import static aespa.groovymap.oauth.util.OAuthType.KAKAO;

import aespa.groovymap.oauth.dto.OAuthLoginRequestDto;
import aespa.groovymap.oauth.dto.OAuthLoginResponseDto;
import aespa.groovymap.oauth.service.OAuthService;
import aespa.groovymap.oauth.util.GoogleUtil;
import aespa.groovymap.oauth.util.KakaoUtil;
import aespa.groovymap.register.dto.RegisterRequestDto;
import aespa.groovymap.register.dto.RegisterResponseDto;
import aespa.groovymap.register.dto.User;
import aespa.groovymap.register.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OAuthController {

    private final KakaoUtil kakaoUtil;
    private final GoogleUtil googleUtil;
    private final OAuthService oauthService;
    private final RegisterService registerService;

    @GetMapping("/login/kakao")
    public String moveToKakaoLogin() {
        String url = kakaoUtil.getKakaoUrl();
        return "redirect:" + url;
    }

    //@GetMapping("/kakao/callback")
    public ResponseEntity loginByKakao(@RequestParam("code") String code, HttpServletRequest request) {
        OAuthLoginResponseDto OAuthLoginResponseDto = oauthService.loginByOAuth(code, request, KAKAO);
        return ResponseEntity.ok(OAuthLoginResponseDto);
    }

    @PostMapping("/kakao/callback")
    public ResponseEntity loginByKakaos(HttpServletRequest request,
                                        @RequestBody OAuthLoginRequestDto oAuthLoginRequestDto) {
        OAuthLoginResponseDto OAuthLoginResponseDto
                = oauthService.loginByOAuth(oAuthLoginRequestDto.getCode(), request, KAKAO);
        return ResponseEntity.ok(OAuthLoginResponseDto);
    }

    @GetMapping("/login/google")
    public String moveToGoogleLogin() {
        String url = googleUtil.getGoogleUrl();
        return "redirect:" + url;
    }

    //@GetMapping("/google/callback")
    public ResponseEntity loginByGoogle(HttpServletRequest request,
                                        @RequestBody OAuthLoginRequestDto oAuthLoginRequestDto) {
        OAuthLoginResponseDto OAuthLoginResponseDto
                = oauthService.loginByOAuth(oAuthLoginRequestDto.getCode(), request, GOOGLE);
        return ResponseEntity.ok(OAuthLoginResponseDto);
    }

    @PostMapping("/google/callback")
    public ResponseEntity loginByGoogles(@RequestParam("code") String code, HttpServletRequest request) {
        OAuthLoginResponseDto OAuthLoginResponseDto = oauthService.loginByOAuth(code, request, GOOGLE);
        return ResponseEntity.ok(OAuthLoginResponseDto);
    }

    @GetMapping("/login/oauth/register")
    public ResponseEntity oauthRegister(@RequestBody RegisterRequestDto registerRequestDto) {
        log.info("소셜 로그인 회원 가입");
        User user = registerService.register(registerRequestDto);
        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setUser(user);
        if (user != null) {
            log.info("소셜 로그인 회원 가입 성공");
            registerResponseDto.setMessage("소셜 로그인 회원가입에 성공했습니다!");
            registerResponseDto.setResult(true);
            return ResponseEntity.ok(registerResponseDto);
        } else {
            log.info("소셜 로그인 회원 가입 실패");
            registerResponseDto.setMessage("소셜 로그인 회원가입에 실패했습니다!");
            registerResponseDto.setResult(false);
            return ResponseEntity.status(409).body(registerResponseDto);
        }
    }
}
