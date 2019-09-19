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

    private final List<ExceptionNoticeRepository> exceptionNoticeRepositorys;
    private final List<ExceptionStatisticRepository> exceptionStatisticRepositorys;

    @Autowired
    public LogpoliceServiceAutoConfiguration(List<ExceptionNoticeRepository> exceptionNoticeRepositorys,
                                             List<ExceptionStatisticRepository> exceptionStatisticRepositorys) {
        this.exceptionNoticeRepositorys = exceptionNoticeRepositorys;
        this.exceptionStatisticRepositorys = exceptionStatisticRepositorys;
    }

    @Bean
    public NoticeService noticeService() {
        Map<NoticeSendEnum, ExceptionNoticeRepository> exceptionNoticeRepositoryMap = exceptionNoticeRepositorys.stream()
                .collect(Collectors.toMap(ExceptionNoticeRepository::getType, Function.identity()));
        Map<NoticeRepositoryEnum, ExceptionStatisticRepository> exceptionStatisticRepositoryMap = exceptionStatisticRepositorys
                .stream().collect(Collectors.toMap(ExceptionStatisticRepository::getType, Function.identity()));
        return new NoticeService(exceptionNoticeRepositoryMap, exceptionStatisticRepositoryMap);
    }
}
