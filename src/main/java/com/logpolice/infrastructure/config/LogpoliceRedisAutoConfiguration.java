package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.rpc.ExceptionStatisticRedis;
import com.logpolice.infrastructure.rpc.LockUtilsRedis;
import com.logpolice.infrastructure.utils.RedisFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * redis配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({LogpoliceJedisAutoConfiguration.class, LogpoliceRedisTemplateAutoConfiguration.class})
@ConditionalOnClass(RedisFactory.class)
public class LogpoliceRedisAutoConfiguration {

    private final List<RedisFactory> redisFactories;

    @Autowired
    public LogpoliceRedisAutoConfiguration(List<RedisFactory> redisFactories) {
        this.redisFactories = redisFactories;
    }

    @Bean
    public ExceptionStatisticRedis exceptionStatisticRedis() {
        return new ExceptionStatisticRedis(redisFactories);
    }

    @Bean
    public LockUtilsRedis lockUtilsRedis() {
        return new LockUtilsRedis(redisFactories);
    }

}
