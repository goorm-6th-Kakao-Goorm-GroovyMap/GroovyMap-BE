package aespa.groovymap.oauth.service;

import static aespa.groovymap.oauth.util.LoginStatus.LOGIN_FAIL;
import static aespa.groovymap.oauth.util.LoginStatus.LOGIN_SUCCESS;
import static aespa.groovymap.oauth.util.LoginStatus.NEED_REGISTER;
import static aespa.groovymap.oauth.util.LoginStatus.SAME_EMAIL;
import static aespa.groovymap.oauth.util.LoginStatus.SAME_NICKNAME;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.domain.Member;
import aespa.groovymap.oauth.dto.KakaoDto;
import aespa.groovymap.oauth.dto.KakaoLoginResponseDto;
import aespa.groovymap.oauth.util.KakaoUtil;
import aespa.groovymap.oauth.util.LoginStatus;
import aespa.groovymap.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoUtil kakaoUtil;
    private final MemberRepository memberRepository;

    public KakaoLoginResponseDto loginByKakao(String code, HttpServletRequest request) {
        KakaoLoginResponseDto kakaoLoginResponseDto = new KakaoLoginResponseDto();

        try {
            String accessToken = kakaoUtil.getAccessToken(code);
            KakaoDto kakaoDto = kakaoUtil.getUserInfoWithToken(accessToken);
            LoginStatus loginStatus = checkMemberRegistration(kakaoDto, request);

            kakaoLoginResponseDto.setEmail(kakaoDto.getEmail());
            kakaoLoginResponseDto.setNickname(kakaoDto.getNickname());
            kakaoLoginResponseDto.setLoginStatus(loginStatus);

            return kakaoLoginResponseDto;
        } catch (JsonProcessingException e) {
            log.info("카카오 로그인 예외로 실패");
            kakaoLoginResponseDto.setEmail("");
            kakaoLoginResponseDto.setNickname("");
            kakaoLoginResponseDto.setLoginStatus(LOGIN_FAIL);
            return kakaoLoginResponseDto;
        }
    }

    private LoginStatus checkMemberRegistration(KakaoDto kakaoDto, HttpServletRequest request) {

        if (memberRepository.findByEmail(kakaoDto.getEmail()).isPresent()) {
            return checkSameEmail(kakaoDto, request);
        } else {
            return checkSameNickname(kakaoDto);
        }
    }

    private LoginStatus checkSameEmail(KakaoDto kakaoDto, HttpServletRequest request) {
        Member sameEmailMember = memberRepository.findByEmail(kakaoDto.getEmail()).get();

        if (sameEmailMember.getPassword().equals(kakaoDto.getNickname())) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConstants.MEMBER_ID, sameEmailMember.getId());
            return LOGIN_SUCCESS;
        } else {
            return SAME_EMAIL;
        }
    }

    private LoginStatus checkSameNickname(KakaoDto kakaoDto) {
        if (memberRepository.findByNickname(kakaoDto.getNickname()).isPresent()) {
            return SAME_NICKNAME;
        } else {
            return NEED_REGISTER;
        }
    }
}
