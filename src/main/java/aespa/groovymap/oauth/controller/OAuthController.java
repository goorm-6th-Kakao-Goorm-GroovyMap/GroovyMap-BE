package aespa.groovymap.oauth.controller;

import aespa.groovymap.oauth.util.KakaoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OAuthController {

    private final KakaoUtil kakaoUtil;

    @GetMapping("/login/kakao")
    public String moveToKakaoLogin() {
        String location = kakaoUtil.getKakaoUrl();

        return "redirect:" + location;
    }
}
