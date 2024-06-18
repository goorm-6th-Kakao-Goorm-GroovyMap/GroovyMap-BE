package aespa.groovymap.util;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;    //Redis에 접근하기 위한 Spring의 Redis 템플릿 클래스

    // 생성자를 통한 의존성 주입
    public RedisUtil(@Qualifier("stringRedisTemplate") StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //지정된 키(key)에 해당하는 데이터를 Redis에서 가져오는 메서드
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    //지정된 키(key)에 값을 저장하는 메서드
    public void setData(String key, String value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    //지정된 키(key)에 값을 저장하고, 지정된 시간(duration) 후에 데이터가 만료되도록 설정하는 메서드
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    // Redis에 지정된 키(key)가 있는지 확인하는 메서드
    public boolean existData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    //지정된 키(key)에 해당하는 데이터를 Redis에서 삭제하는 메서드
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}