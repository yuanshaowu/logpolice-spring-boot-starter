package com.logpolice.application;

import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.entity.NoticeFrequencyType;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeRepositoryEnum;
import com.logpolice.infrastructure.enums.NoticeSendEnum;
import com.logpolice.infrastructure.exception.RepositoryNotExistException;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
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

    private final List<ExceptionNoticeRepository> exceptionNoticeRepositorys;
    private final List<ExceptionStatisticRepository> exceptionStatisticRepositorys;

    public NoticeService(List<ExceptionNoticeRepository> exceptionNoticeRepositorys,
                         List<ExceptionStatisticRepository> exceptionStatisticRepositorys) {
        this.exceptionNoticeRepositorys = exceptionNoticeRepositorys;
        this.exceptionStatisticRepositorys = exceptionStatisticRepositorys;
    }

    public void send(ExceptionNotice exceptionNotice, LogpoliceProperties logpoliceProperties) {
        // 判断是否包含白名单
        if (exceptionNotice.containsWhiteList(logpoliceProperties.getExceptionWhiteList(), logpoliceProperties.getClassWhiteList())) {
            log.warn("logSendAppender.append exceptionWhiteList skip, exception:{}", exceptionNotice);
            return;
        }

        ExceptionNoticeRepository noticeRepository = getExceptionNoticeRepository(logpoliceProperties.getNoticeSendType());
        ExceptionStatisticRepository statisticRepository = getExceptionStatisticRepository(logpoliceProperties.getEnableRedisStorage());

        String openId = exceptionNotice.getOpenId();
        ExceptionStatistic exceptionStatistic = statisticRepository.findByOpenId(openId)
                .orElse(new ExceptionStatistic(openId));
        statisticRepository.save(openId, exceptionStatistic);

        // 判断异常数据是否超时重置
        if (exceptionStatistic.isTimeOut(logpoliceProperties.getCleanTimeInterval())) {
            log.info("noticeService.send timeOut,prepare resetData openId:{}", openId);
            exceptionStatistic.reset();
        }
        exceptionStatistic.pushOne();

        // 判断异常数据是否符合推送
        if (NoticeFrequencyType.check(exceptionStatistic, logpoliceProperties)) {
            log.info("noticeService.send prepare, openId:{}", openId);
            exceptionStatistic.updateData();
            exceptionNotice.updateData(exceptionStatistic.getShowCount(),
                    exceptionStatistic.getNoticeTime(),
                    exceptionStatistic.getFirstTime());
            noticeRepository.send(exceptionNotice);
        }
        statisticRepository.save(openId, exceptionStatistic);
    }

    private ExceptionNoticeRepository getExceptionNoticeRepository(NoticeSendEnum noticeSendEnum) {
        return exceptionNoticeRepositorys.stream()
                .filter(e -> Objects.equals(e.getType(), noticeSendEnum))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("noticeService.getExceptionNoticeRepository not exist"));
    }

    private ExceptionStatisticRepository getExceptionStatisticRepository(Boolean enableRedisStorage) {
        return exceptionStatisticRepositorys.stream()
                .filter(e -> Objects.equals(e.getType(), NoticeRepositoryEnum.getType(enableRedisStorage)))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotExistException("noticeService.getExceptionStatisticRepository not exist"));
    }
}
