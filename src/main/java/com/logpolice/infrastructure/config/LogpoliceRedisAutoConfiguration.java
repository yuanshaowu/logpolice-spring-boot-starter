package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.rpc.ExceptionStatisticRedis;
import com.logpolice.infrastructure.rpc.LockUtilsRedis;
import com.logpolice.infrastructure.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisSentinelPool;

/**
 * redis配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({LogpoliceMailAutoConfiguration.class, LogpoliceDingDingAutoConfiguration.class})
@ConditionalOnClass(JedisSentinelPool.class)
public class LogpoliceRedisAutoConfiguration {

    private final JedisSentinelPool jedisSentinelPool;

    @Autowired
    public LogpoliceRedisAutoConfiguration(JedisSentinelPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
    }

    @Bean
    public RedisUtils redisUtils() {
        return new RedisUtils(jedisSentinelPool);
    }

    @Bean
    public ExceptionStatisticRedis exceptionStatisticRedis() {
        return new ExceptionStatisticRedis(redisUtils());
    }

    @Bean
    public LockUtilsRedis lockUtilsRedis() {
        return new LockUtilsRedis(redisUtils());
    }
}
