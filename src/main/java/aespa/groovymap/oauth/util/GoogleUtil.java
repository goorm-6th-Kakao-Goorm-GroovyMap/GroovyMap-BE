package aespa.groovymap.oauth.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public String getGoogleUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + client_id + "&redirect_uri=" + redirect_uri
                + "&response_type=" + "code" + "&scope=" + "email%20profile";

    }

}
