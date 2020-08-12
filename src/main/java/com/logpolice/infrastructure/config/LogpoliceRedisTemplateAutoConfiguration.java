package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.utils.redis.RedisTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@Import(RedisAutoConfiguration.class)
public class LogpoliceRedisTemplateAutoConfiguration {

    private final RedisTemplate redisTemplate;

    @Autowired
    public LogpoliceRedisTemplateAutoConfiguration(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public RedisTemplateUtils redisTemplateUtils() {
        return new RedisTemplateUtils(redisTemplate);
    }

}
