package com.logpolice.infrastructure.config;

import com.logpolice.application.NoticeService;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeRepositoryEnum;
import com.logpolice.infrastructure.enums.NoticeSendEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 本地缓存自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({LogpoliceRedisAutoConfiguration.class, LogpoliceLoclCacheAutoConfiguration.class,
        LogpoliceMailAutoConfiguration.class, LogpoliceDingDingAutoConfiguration.class})
public class LogpoliceServiceAutoConfiguration {

    private final List<ExceptionNoticeRepository> exceptionNoticeRepositories;
    private final List<ExceptionStatisticRepository> exceptionStatisticRepositories;

    @Autowired
    public LogpoliceServiceAutoConfiguration(List<ExceptionNoticeRepository> exceptionNoticeRepositories,
                                             List<ExceptionStatisticRepository> exceptionStatisticRepositories) {
        this.exceptionNoticeRepositories = exceptionNoticeRepositories;
        this.exceptionStatisticRepositories = exceptionStatisticRepositories;
    }

    @Bean
    public NoticeService noticeService() {
        return new NoticeService(exceptionNoticeRepositories, exceptionStatisticRepositories);
    }
}
