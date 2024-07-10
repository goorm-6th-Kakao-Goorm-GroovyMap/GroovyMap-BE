package aespa.groovymap.dm.config;

import aespa.groovymap.config.SessionConstants;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
        log.info("메시지 브로커 구성 완료");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:3000", "http://groovymap.store",
                        "https://groovy-map-git-develop-soyeons-projects-ec6b0062.vercel.app",
                        "https://groovymap.vercel.app/", "https://groovymap.store")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
        log.info("STOMP 엔드포인트 등록 완료: /ws");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                    if (sessionAttributes != null && sessionAttributes.containsKey(SessionConstants.MEMBER_ID)) {
                        Long memberId = (Long) sessionAttributes.get(SessionConstants.MEMBER_ID);
                        accessor.setUser(new UserPrincipal(memberId));
                        log.info("WebSocket 연결 성공: 사용자 ID = {}", memberId);
                    } else {
                        log.error("WebSocket 연결 실패: 세션에서 사용자 ID를 찾을 수 없음");
                        throw new IllegalStateException("인증되지 않은 사용자");
                    }
                }
                return message;
            }
        });
        log.info("WebSocket 인증 인터셉터 등록 완료");
    }
}