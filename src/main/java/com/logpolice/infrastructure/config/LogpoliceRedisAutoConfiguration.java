package com.logpolice.infrastructure.config;

import com.logpolice.application.NoticeService;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
import com.logpolice.infrastructure.rpc.ExceptionStatisticRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@EnableConfigurationProperties(LogpoliceProperties.class)
@AutoConfigureAfter({LogpoliceMailAutoConfiguration.class, LogpoliceDingDingAutoConfiguration.class})
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(name = "logpolice.enable-redis-storage", havingValue = "true")
public class LogpoliceRedisAutoConfiguration {

    private final RedisTemplate redisTemplate;
    private final ExceptionNoticeRepository exceptionNoticeRepository;

    @Autowired
    public LogpoliceRedisAutoConfiguration(RedisTemplate redisTemplate,
                                           ExceptionNoticeRepository exceptionNoticeRepository) {
        this.redisTemplate = redisTemplate;
        this.exceptionNoticeRepository = exceptionNoticeRepository;
    }

    @Bean
    public ExceptionStatisticRedis exceptionStatisticRedis() {
        return new ExceptionStatisticRedis(redisTemplate);
    }

    @Bean
    public NoticeService noticeService() {
        return new NoticeService(exceptionNoticeRepository, exceptionStatisticRedis());
    }
}
