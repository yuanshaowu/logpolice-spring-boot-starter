package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.rpc.ExceptionStatisticRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({LogpoliceMailAutoConfiguration.class, LogpoliceDingDingAutoConfiguration.class})
@ConditionalOnClass(RedisTemplate.class)
public class LogpoliceRedisAutoConfiguration {

    private final RedisTemplate redisTemplate;

    @Autowired
    public LogpoliceRedisAutoConfiguration(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public ExceptionStatisticRedis exceptionStatisticRedis() {
        return new ExceptionStatisticRedis(redisTemplate);
    }
}
