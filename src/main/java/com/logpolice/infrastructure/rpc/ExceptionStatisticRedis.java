package com.logpolice.infrastructure.rpc;

import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeRepositoryEnum;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 异常统计本地缓存
 *
 * @author huang
 * @date 2019/9/11
 */
@Slf4j
public class ExceptionStatisticRedis implements ExceptionStatisticRepository {

    private final String REDIS_KEY_VERSION = "_version";

    private final RedisTemplate<String, Object> redisTemplate;

    public ExceptionStatisticRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public NoticeRepositoryEnum getType() {
        return NoticeRepositoryEnum.REDIS;
    }

    @Override
    public Optional<ExceptionStatistic> findByOpenId(String openId) {
        ExceptionStatistic exceptionStatistic = null;
        Object obj = redisTemplate.opsForValue().get(openId);
        if (Objects.nonNull(obj) && obj instanceof ExceptionStatistic) {
            exceptionStatistic = (ExceptionStatistic) obj;
        }
        return Optional.ofNullable(exceptionStatistic);
    }

    @Override
    public boolean save(String openId, ExceptionStatistic exceptionStatistic) {
        boolean success;
        if (success = lock(openId)) {
            try {
                redisTemplate.opsForValue().set(openId, exceptionStatistic, LogpoliceConstant.CLEAN_TIME_INTERVAL, TimeUnit.SECONDS);
            } finally {
                unlock(openId);
            }
        }
        return success;
    }

    private boolean lock(String openId) {
        Long increment = 1L;
        String redisKey = openId + REDIS_KEY_VERSION;
        Long remoteVersion = redisTemplate.opsForValue().increment(redisKey, increment);
        if (Objects.equals(remoteVersion, increment)) {
            return true;
        }
        long versionMax = 20L;
        if (remoteVersion > versionMax) {
            redisTemplate.delete(redisKey);
        }
        return false;
    }

    private void unlock(String openId) {
        redisTemplate.delete(openId + REDIS_KEY_VERSION);
    }
}
