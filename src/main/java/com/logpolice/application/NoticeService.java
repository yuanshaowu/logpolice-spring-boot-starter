package com.logpolice.application;

import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.entity.NoticeFrequencyType;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.enums.NoticeSendEnum;
import com.logpolice.infrastructure.exception.RepositoryNotExistException;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
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
public class NoticeService {

    private final List<ExceptionNoticeRepository> exceptionNoticeRepositories;
    private final List<ExceptionStatisticRepository> exceptionStatisticRepositories;
    private final List<LockUtils> lockUtils;

    public NoticeService(List<ExceptionNoticeRepository> exceptionNoticeRepositories,
                         List<ExceptionStatisticRepository> exceptionStatisticRepositories,
                         List<LockUtils> lockUtils) {
        this.exceptionNoticeRepositories = exceptionNoticeRepositories;
        this.exceptionStatisticRepositories = exceptionStatisticRepositories;
        this.lockUtils = lockUtils;
    }

    public void send(ExceptionNotice exceptionNotice, LogpoliceProperties logpoliceProperties, int currentRetryNum) {
        boolean lock;
        currentRetryNum += 1;
        String openId = exceptionNotice.getOpenId();
        LockUtils lockUtils = this.getLockUtils(logpoliceProperties.getEnableRedisStorage());

        // 判断加锁状态，递归最多重试三次
        if (lock = lockUtils.lock(openId)) {
            try {
                exceptionNotice = this.create(exceptionNotice, logpoliceProperties);
            } finally {
                lockUtils.unlock(openId);
            }
        }
        if (!lock && currentRetryNum <= LogpoliceConstant.LOCK_MAX_RETRY_NUM) {
            this.send(exceptionNotice, logpoliceProperties, currentRetryNum);
        }

        // 判断是否符合推送条件，可以优化异步推送
        if (lock && exceptionNotice.isSend()) {
            ExceptionNoticeRepository noticeRepository = this.getExceptionNoticeRepository(logpoliceProperties.getNoticeSendType());
            noticeRepository.send(exceptionNotice);
            log.info("noticeService.send success, openId:{}", openId);
        }
    }

    private ExceptionNotice create(ExceptionNotice exceptionNotice, LogpoliceProperties logpoliceProperties) {
        // 判断是否包含白名单
        if (exceptionNotice.containsWhiteList(logpoliceProperties.getExceptionWhiteList(), logpoliceProperties.getClassWhiteList())) {
            log.warn("logSendAppender.append exceptionWhiteList skip, exception:{}", exceptionNotice);
            return exceptionNotice;
        }

        // 查询异常数据，不存在则初始化
        ExceptionStatisticRepository statisticRepository = this.getExceptionStatisticRepository(logpoliceProperties.getEnableRedisStorage());
        String openId = exceptionNotice.getOpenId();
        ExceptionStatistic exceptionStatistic = statisticRepository.findByOpenId(openId)
                .orElse(new ExceptionStatistic(openId));

        // 判断异常数据是否超时重置
        if (exceptionStatistic.isTimeOut(logpoliceProperties.getCleanTimeInterval())) {
            log.info("noticeService.send timeOut, prepare resetData openId:{}", openId);
            exceptionStatistic.reset();
        }
        exceptionStatistic.pushOne();

        // 判断异常数据是否符合推送
        boolean isCheck;
        if (isCheck = NoticeFrequencyType.check(exceptionStatistic, logpoliceProperties)) {
            log.info("noticeService.send prepare, openId:{}", openId);
            exceptionStatistic.updateData();
            exceptionNotice.updateData(exceptionStatistic.getShowCount(),
                    exceptionStatistic.getNoticeTime(),
                    exceptionStatistic.getFirstTime());
        }
        boolean isSuccess = statisticRepository.save(openId, exceptionStatistic);
        exceptionNotice.updateSend(isCheck, isSuccess);
        return exceptionNotice;
    }

    private ExceptionNoticeRepository getExceptionNoticeRepository(NoticeSendEnum noticeSendEnum) {
        return exceptionNoticeRepositories.stream()
                .filter(e -> Objects.equals(e.getType(), noticeSendEnum))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("noticeService.getExceptionNoticeRepository not exist"));
    }

    private ExceptionStatisticRepository getExceptionStatisticRepository(Boolean enableRedisStorage) {
        return exceptionStatisticRepositories.stream()
                .filter(e -> Objects.equals(e.getType(), NoticeDbTypeEnum.getType(enableRedisStorage)))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("noticeService.getExceptionStatisticRepository not exist"));
    }

    private LockUtils getLockUtils(Boolean enableRedisStorage) {
        return lockUtils.stream()
                .filter(e -> Objects.equals(e.getType(), NoticeDbTypeEnum.getType(enableRedisStorage)))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("noticeService.getExceptionStatisticRepository not exist"));
    }
}
