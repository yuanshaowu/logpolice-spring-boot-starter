package com.logpolice.infrastructure.config;

import com.logpolice.application.NoticeService;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
import com.logpolice.infrastructure.rpc.ExceptionStatisticLocalCache;
import com.logpolice.infrastructure.rpc.ExceptionStatisticRedis;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({LogpoliceMailAutoConfiguration.class, LogpoliceDingDingAutoConfiguration.class})
@ConditionalOnMissingBean(ExceptionStatisticRedis.class)
public class LogpoliceLoclCacheAutoConfiguration {

    private final ExceptionNoticeRepository exceptionNoticeRepository;

    public LogpoliceLoclCacheAutoConfiguration(ExceptionNoticeRepository exceptionNoticeRepository) {
        this.exceptionNoticeRepository = exceptionNoticeRepository;
    }

    @Bean
    public ExceptionStatisticLocalCache exceptionStatisticLocalCache() {
        return new ExceptionStatisticLocalCache(new ConcurrentHashMap<>());
    }

    @Bean
    public NoticeService noticeService() {
        return new NoticeService(exceptionNoticeRepository,exceptionStatisticLocalCache());
    }
}
