package aespa.groovymap.oauth.util;

import aespa.groovymap.oauth.dto.OAuthLoginInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUtil {

    @Value("${kakao.client.id}")
    private String client_id;

    @Value("${kakao.client.secret")
    private String client_secret;

    @Value("${kakao.redirect.uri}")
    private String redirect_uri;

    public String getKakaoUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + client_id + "&redirect_uri=" + redirect_uri;
    }

    public String getAccessToken(String code) throws JsonProcessingException {
        String accessToken = "";
        String refreshToken = "";
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        // 1. header 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 2. body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //고정값
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("redirect_uri", redirect_uri); //등록한 redirect uri
        params.add("code", code);

        // 3. header + body
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);

        // 4. http 요청하기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                reqUrl,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        // 5. 응답으로 온 json 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        accessToken = root.path("access_token").asText();
        refreshToken = root.path("refresh_token").asText();

        return accessToken;
    }

    public OAuthLoginInfoDto getUserInfoWithToken(String accessToken) throws JsonProcessingException {
        String nickname = "";
        String email = "";
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        //1. HttpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //2. HttpHeader 담기
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        //3. HTTP 요청하기
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                reqUrl,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        //4. Response 데이터 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        JsonNode kakaoAccount = root.path("kakao_account");
        JsonNode profile = kakaoAccount.path("profile");

        email = kakaoAccount.path("email").asText(); // asText 사용, 없으면 빈 문자열 반환
        nickname = profile.path("nickname").asText(); // asText 사용, 없으면 빈 문자열 반환

        return new OAuthLoginInfoDto(nickname, email);
    }
}
