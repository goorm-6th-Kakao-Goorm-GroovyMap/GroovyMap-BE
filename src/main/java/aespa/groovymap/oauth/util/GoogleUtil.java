package aespa.groovymap.oauth.util;

import aespa.groovymap.oauth.dto.OAuthLoginInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleUtil {

    @Value("${google.client.id}")
    private String client_id;

    @Value("${google.client.secret}")
    private String client_secret;

    @Value("${google.redirect.uri}")
    private String redirect_uri;

    private final String google_token_uri = "https://oauth2.googleapis.com/token";

    private final String google_info_uri = "https://oauth2.googleapis.com/tokeninfo";

    public String getGoogleUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + client_id + "&redirect_uri=" + redirect_uri
                + "&response_type=" + "code" + "&scope=" + "email%20profile";
    }

    public String getAccessToken(String code) throws JsonProcessingException {
        String accessToken = "";
        String refreshToken = "";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("code", code);
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);
        params.put("redirect_uri", redirect_uri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> response = restTemplate.postForEntity(google_token_uri, params, String.class);

        // 5. 응답으로 온 json 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        accessToken = root.path("id_token").asText();

        return accessToken;
    }

    public OAuthLoginInfoDto getUserInfoWithToken(String accessToken) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("id_token", accessToken);

        ResponseEntity<String> response = restTemplate.postForEntity(google_info_uri, params, String.class);

        // 5. 응답으로 온 json 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        String email = root.path("email").asText();
        String nickname = root.path("name").asText();

        return new OAuthLoginInfoDto(nickname, email);
    }

}
