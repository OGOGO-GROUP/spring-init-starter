package kg.ogogo.academy.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedisJwtService {

    private final Logger log = LoggerFactory.getLogger(RedisJwtService.class);

    private static final String JWT = "JWT-ACADEMY";

    private HashOperations<String, String, JWTToken> hashOperations;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisJwtService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void initialize(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void putToken(String username, JWTToken jwtToken){
        deleteToken(username);
        hashOperations.put(JWT, username, jwtToken);
        log.debug("Inserted new token of user: {}", username);
    }

    public JWTToken getTokenByUsername(String username){
        return hashOperations.get(JWT, username);
    }

    public void deleteToken(String username){
        hashOperations.delete(JWT,username);
        log.debug("Deleted token of user: {}", username);
    }


}
