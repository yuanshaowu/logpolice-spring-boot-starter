package com.logpolice.infrastructure.utils.redis;

import com.alibaba.fastjson.JSONObject;
import com.logpolice.infrastructure.enums.RedisClientTypeEnum;
import com.logpolice.infrastructure.utils.RedisFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * redis原生客户端
 *
 * @author huang
 * @date 2020/5/27
 */
public class RedisTemplateUtils implements RedisFactory {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTemplateUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RedisClientTypeEnum getType() {
        return RedisClientTypeEnum.REDIS_TEMPLATE;
    }

    @Override
    public String get(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return Objects.nonNull(obj) ? JSONObject.toJSONString(obj) : null;
    }

    @Override
    public String setex(String key, int seconds, String value) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
        return "1";
    }

    @Override
    public boolean lock(String lockKey, String value, int lockSecond) {
        return (Boolean) redisTemplate.execute((RedisCallback) connection ->
                connection.set(lockKey.getBytes(), value.getBytes(), Expiration.seconds(lockSecond), RedisStringCommands.SetOption.SET_IF_ABSENT)
        );
    }

    @Override
    public Long del(String key) {
        redisTemplate.delete(key);
        return 1L;
    }

}
