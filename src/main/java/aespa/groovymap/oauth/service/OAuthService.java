package aespa.groovymap.oauth.service;

import static aespa.groovymap.oauth.util.LoginStatus.LOGIN_FAIL;
import static aespa.groovymap.oauth.util.LoginStatus.LOGIN_SUCCESS;
import static aespa.groovymap.oauth.util.LoginStatus.NEED_REGISTER;
import static aespa.groovymap.oauth.util.LoginStatus.SAME_EMAIL;
import static aespa.groovymap.oauth.util.LoginStatus.SAME_NICKNAME;
import static aespa.groovymap.oauth.util.OAuthType.GOOGLE;
import static aespa.groovymap.oauth.util.OAuthType.KAKAO;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.domain.Member;
import aespa.groovymap.oauth.dto.OAuthLoginInfoDto;
import aespa.groovymap.oauth.dto.OAuthLoginResponseDto;
import aespa.groovymap.oauth.util.GoogleUtil;
import aespa.groovymap.oauth.util.KakaoUtil;
import aespa.groovymap.oauth.util.LoginStatus;
import aespa.groovymap.oauth.util.OAuthType;
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
    private final GoogleUtil googleUtil;
    private final MemberRepository memberRepository;

    public OAuthLoginResponseDto loginByOAuth(String code, HttpServletRequest request, OAuthType oauthType) {

        try {
            OAuthLoginInfoDto OAuthLoginInfoDto = getOAuthLoginInfoDto(code, oauthType);
            LoginStatus loginStatus = checkMemberRegistration(OAuthLoginInfoDto, request);
            log.info(OAuthLoginInfoDto.getEmail() + " " + OAuthLoginInfoDto.getNickname() + " " + loginStatus);
            return makeOAuthResponse(OAuthLoginInfoDto, loginStatus);
        } catch (JsonProcessingException e) {
            log.info(oauthType + " 로그인 예외로 실패");
            return makeOAuthResponse(new OAuthLoginInfoDto("", ""), LOGIN_FAIL);
        }
    }

    private OAuthLoginInfoDto getOAuthLoginInfoDto(String code, OAuthType oauthType) throws JsonProcessingException {
        String accessToken;
        OAuthLoginInfoDto OAuthLoginInfoDto = null;
        if (oauthType == KAKAO) {
            accessToken = kakaoUtil.getAccessToken(code);
            OAuthLoginInfoDto = kakaoUtil.getUserInfoWithToken(accessToken);
        } else if (oauthType == GOOGLE) {
            accessToken = googleUtil.getAccessToken(code);
            OAuthLoginInfoDto = googleUtil.getUserInfoWithToken(accessToken);
        }
        return OAuthLoginInfoDto;
    }

    private OAuthLoginResponseDto makeOAuthResponse(OAuthLoginInfoDto oAuthLoginInfoDto, LoginStatus loginStatus) {
        OAuthLoginResponseDto oAuthLoginResponseDto = new OAuthLoginResponseDto();
        oAuthLoginResponseDto.setEmail(oAuthLoginInfoDto.getEmail());
        oAuthLoginResponseDto.setNickname(oAuthLoginInfoDto.getNickname());
        oAuthLoginResponseDto.setLoginStatus(loginStatus);
        return oAuthLoginResponseDto;
    }

    private LoginStatus checkMemberRegistration(OAuthLoginInfoDto OAuthLoginInfoDto, HttpServletRequest request) {

        if (memberRepository.findByEmail(OAuthLoginInfoDto.getEmail()).isPresent()) {
            return checkSameEmail(OAuthLoginInfoDto, request);
        } else {
            return checkSameNickname(OAuthLoginInfoDto);
        }
    }

    private LoginStatus checkSameEmail(OAuthLoginInfoDto OAuthLoginInfoDto, HttpServletRequest request) {
        Member sameEmailMember = memberRepository.findByEmail(OAuthLoginInfoDto.getEmail()).get();

        if (sameEmailMember.getPassword().equals(OAuthLoginInfoDto.getNickname())) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConstants.MEMBER_ID, sameEmailMember.getId());
            return LOGIN_SUCCESS;
        } else {
            return SAME_EMAIL;
        }
    }

    private LoginStatus checkSameNickname(OAuthLoginInfoDto OAuthLoginInfoDto) {
        if (memberRepository.findByNickname(OAuthLoginInfoDto.getNickname()).isPresent()) {
            return SAME_NICKNAME;
        } else {
            return NEED_REGISTER;
        }
    }
}
