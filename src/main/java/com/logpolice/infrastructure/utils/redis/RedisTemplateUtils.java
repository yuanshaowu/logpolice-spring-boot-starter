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

    private static final String PRE = "logpolice_spring_boot_starter_";
    private static final String PRE_LOCK = "logpolice_spring_boot_starter_lock_";

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
        Object obj = redisTemplate.opsForValue().get(PRE + key);
        return Objects.nonNull(obj) ? obj instanceof String ? (String) obj : JSONObject.toJSONString(obj) : null;
    }

    @Override
    public boolean setex(String key, String value, int seconds) {
        redisTemplate.opsForValue().set(PRE + key, value, seconds, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean lock(String key, String value, int seconds) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.set((PRE_LOCK + key).getBytes(),
                        value.getBytes(),
                        Expiration.seconds(seconds),
                        RedisStringCommands.SetOption.SET_IF_ABSENT)
        );
    }

    @Override
    public boolean unlock(String key) {
        redisTemplate.delete(PRE_LOCK + key);
        return true;
    }

}
