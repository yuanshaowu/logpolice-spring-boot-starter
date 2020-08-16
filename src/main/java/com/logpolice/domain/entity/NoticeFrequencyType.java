package com.logpolice.domain.entity;

import com.logpolice.infrastructure.properties.LogpoliceProperties;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 消息推送策略
 *
 * @author huang
 * @date 2019/8/28
 */
public enum NoticeFrequencyType {

    /**
     * TIMEOUT：超时
     * SHOW_COUNT：次数
     */
    TIMEOUT {
        @Override
        public boolean checkSend(ExceptionStatistic exceptionStatistics, LogpoliceProperties logpoliceProperties) {
            Duration dur = Duration.between(exceptionStatistics.getNoticeTime(), LocalDateTime.now());
            return Duration.ofSeconds(logpoliceProperties.getTimeInterval()).compareTo(dur) < 0;
        }
    },
    SHOW_COUNT {
        @Override
        public boolean checkSend(ExceptionStatistic exceptionStatistics, LogpoliceProperties logpoliceProperties) {
            long noticeShowCount = exceptionStatistics.getShowCount().longValue() - exceptionStatistics.getLastShowedCount();
            return noticeShowCount > logpoliceProperties.getShowCount();
        }

    };

    public boolean checkSend(ExceptionStatistic exceptionStatistics, LogpoliceProperties logpoliceProperties) {
        return false;
    }

    public static boolean check(ExceptionStatistic exceptionStatistic, LogpoliceProperties logpoliceProperties) {
        NoticeFrequencyType frequencyType = logpoliceProperties.getFrequencyType();
        return exceptionStatistic.isFirst() || frequencyType.checkSend(exceptionStatistic, logpoliceProperties);
    }
}
