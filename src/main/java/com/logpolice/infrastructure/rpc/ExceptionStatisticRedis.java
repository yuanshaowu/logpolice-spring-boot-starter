package com.logpolice.infrastructure.rpc;

import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final LogpoliceProperties logpoliceProperties;

    public ExceptionStatisticRedis(RedisTemplate<String, Object> redisTemplate,
                                   LogpoliceProperties logpoliceProperties) {
        this.redisTemplate = redisTemplate;
        this.logpoliceProperties = logpoliceProperties;
    }

    @Override
    public Optional<ExceptionStatistic> findByOpenId(String openId) {
        String key = logpoliceProperties.getExceptionRedisKey() + openId;
        ExceptionStatistic exceptionStatistic = null;
        Object obj = redisTemplate.opsForValue().get(key);
        if (Objects.nonNull(obj) && obj instanceof ExceptionStatistic) {
            exceptionStatistic = (ExceptionStatistic) obj;
        }
        return Optional.ofNullable(exceptionStatistic);
    }

    @Override
    public void save(String openId, ExceptionStatistic exceptionStatistic) {
        String key = this.logpoliceProperties.getExceptionRedisKey() + openId;
        redisTemplate.opsForValue().set(key, exceptionStatistic, LogpoliceConstant.CLEAN_TIME_INTERVAL, TimeUnit.SECONDS);
    }
}
