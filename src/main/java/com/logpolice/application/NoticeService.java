package com.logpolice.application;

import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.entity.NoticeFrequencyType;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
import com.logpolice.infrastructure.utils.LockUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息逻辑层
 *
 * @author huang
 * @date 2019/9/10
 */
@Slf4j
public class NoticeService {

    private final NoticeServiceFactory noticeServiceFactory;

    public NoticeService(NoticeServiceFactory noticeServiceFactory) {
        this.noticeServiceFactory = noticeServiceFactory;
    }

    public void send(ExceptionNotice exceptionNotice, LogpoliceProperties logpoliceProperties) {
        // 根据数据存储类型获取锁工具
        boolean succeed = false;
        String openId = exceptionNotice.getOpenId();
        LockUtils lockUtils = noticeServiceFactory.getLockUtils(logpoliceProperties);
        for (int i = 0; i < LogpoliceConstant.LOCK_MAX_RETRY_NUM; i++) {
            try {
                if (!lockUtils.lock(openId)) {
                    continue;
                }
                exceptionNotice = this.saveExceptionNotice(exceptionNotice, logpoliceProperties);
                succeed = true;
                break;
            } finally {
                lockUtils.unlock(openId);
            }
        }

        // 判断是否符合推送条件，可以优化异步推送
        if (succeed && exceptionNotice.isSend()) {
            ExceptionNoticeRepository noticeRepository = noticeServiceFactory.getExceptionNoticeRepository(logpoliceProperties);
            noticeRepository.send(exceptionNotice);
            log.info("noticeService.send success, openId:{}", openId);
        }
    }

    private ExceptionNotice saveExceptionNotice(ExceptionNotice exceptionNotice, LogpoliceProperties logpoliceProperties) {
        // 判断是否包含白名单
        if (exceptionNotice.containsWhiteList(logpoliceProperties.getExceptionWhiteList(), logpoliceProperties.getClassWhiteList())) {
            log.warn("logSendAppender.append exceptionWhiteList skip, exception:{}", exceptionNotice);
            return exceptionNotice;
        }

        // 查询异常数据，不存在则初始化
        ExceptionStatisticRepository statisticRepository = noticeServiceFactory.getExceptionStatisticRepository(logpoliceProperties);
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

}
