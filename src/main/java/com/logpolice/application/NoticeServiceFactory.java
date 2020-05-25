package com.logpolice.application;

import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.exception.RepositoryNotExistException;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
import com.logpolice.infrastructure.utils.LockUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * 消息逻辑层
 *
 * @author huang
 * @date 2019/9/10
 */
@Slf4j
public class NoticeServiceFactory {

    private final List<ExceptionNoticeRepository> exceptionNoticeRepositories;
    private final List<ExceptionStatisticRepository> exceptionStatisticRepositories;
    private final List<LockUtils> lockUtils;

    public NoticeServiceFactory(List<ExceptionNoticeRepository> exceptionNoticeRepositories,
                                List<ExceptionStatisticRepository> exceptionStatisticRepositories,
                                List<LockUtils> lockUtils) {
        this.exceptionNoticeRepositories = exceptionNoticeRepositories;
        this.exceptionStatisticRepositories = exceptionStatisticRepositories;
        this.lockUtils = lockUtils;
    }

    public ExceptionNoticeRepository getExceptionNoticeRepository(LogpoliceProperties logpoliceProperties) {
        return exceptionNoticeRepositories.stream()
                .filter(e -> Objects.equals(e.getType(), logpoliceProperties.getNoticeSendType()))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("NoticeServiceFactory.getExceptionNoticeRepository not exist"));
    }

    public ExceptionStatisticRepository getExceptionStatisticRepository(LogpoliceProperties logpoliceProperties) {
        return exceptionStatisticRepositories.stream()
                .filter(e -> Objects.equals(e.getType(), NoticeDbTypeEnum.getType(logpoliceProperties.getEnableRedisStorage())))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("NoticeServiceFactory.getExceptionStatisticRepository not exist"));
    }

    public LockUtils getLockUtils(LogpoliceProperties logpoliceProperties) {
        return lockUtils.stream()
                .filter(e -> Objects.equals(e.getType(), NoticeDbTypeEnum.getType(logpoliceProperties.getEnableRedisStorage())))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("NoticeServiceFactory.getExceptionStatisticRepository not exist"));
    }
}
