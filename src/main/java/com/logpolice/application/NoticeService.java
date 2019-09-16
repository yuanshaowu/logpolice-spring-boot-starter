package com.logpolice.application;

import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.entity.NoticeFrequencyType;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 消息逻辑层
 *
 * @author huang
 * @date 2019/9/10
 */
@Slf4j
public class NoticeService {

    private final ExceptionNoticeRepository exceptionNoticeRepository;
    private final ExceptionStatisticRepository exceptionStatisticRepository;

    public NoticeService(ExceptionNoticeRepository exceptionNoticeRepository,
                         ExceptionStatisticRepository exceptionStatisticRepository) {
        this.exceptionNoticeRepository = exceptionNoticeRepository;
        this.exceptionStatisticRepository = exceptionStatisticRepository;
    }

    public void send(ExceptionNotice exceptionNotice, LogpoliceProperties logpoliceProperties) {
        String openId = exceptionNotice.getOpenId();
        ExceptionStatistic exceptionStatistic = exceptionStatisticRepository.findByOpenId(openId)
                .orElse(new ExceptionStatistic(openId));
        log.info("noticeService.send start, openId:{}, exceptionStatistic:{}", openId, exceptionStatistic);

        if (NoticeFrequencyType.checkTimeOut(exceptionStatistic, logpoliceProperties)) {
            log.info("noticeService.send timeOut,prepare resetData openId:{}", openId);
            exceptionStatistic.resetData();
        }
        Long showCount = exceptionStatistic.pushOne();

        NoticeFrequencyType frequencyType = logpoliceProperties.getFrequencyType();
        if (exceptionStatistic.isFirst() || frequencyType.checkSend(exceptionStatistic, logpoliceProperties)) {
            log.info("noticeService.send prepare, openId:{}", openId);
            LocalDateTime now = LocalDateTime.now();
            exceptionStatistic.updateData(showCount, now);
            exceptionNotice.updateData(showCount, now, exceptionStatistic.getFirstTime());
            exceptionNoticeRepository.send(exceptionNotice);
        }
        exceptionStatisticRepository.save(openId, exceptionStatistic);
    }
}
