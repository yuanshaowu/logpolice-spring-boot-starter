package com.logpolice.infrastructure.rpc;

import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.utils.LockUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * redis锁
 *
 * @author huang
 * @date 2019/9/3
 */
public class LockUtilsRedis implements LockUtils {

    private final static String REDIS_VERSION = "_version";

    private final RedisTemplate<String, Object> redisTemplate;

    public LockUtilsRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public NoticeDbTypeEnum getType() {
        return NoticeDbTypeEnum.REDIS;
    }

    @Override
    public boolean lock(String key) {
        boolean success;
        long maxLimit = 20L;
        String redisKey = key + REDIS_VERSION;
        Long version = redisTemplate.opsForValue().increment(redisKey, 1L);
        if (success = Objects.equals(version, 1L)) {
            redisTemplate.expire(redisKey, 1L, TimeUnit.SECONDS);
        }
        if (version > maxLimit) {
            redisTemplate.delete(key + REDIS_VERSION);
        }
        return success;
    }

    @Override
    public void unlock(String key) {
        redisTemplate.delete(key + REDIS_VERSION);
    }
}
