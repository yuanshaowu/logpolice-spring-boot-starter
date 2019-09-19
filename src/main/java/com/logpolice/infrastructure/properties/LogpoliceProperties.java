package com.logpolice.infrastructure.properties;

import com.logpolice.domain.entity.NoticeFrequencyType;
import com.logpolice.infrastructure.enums.NoticeSendEnum;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志报警配置
 *
 * @author huang
 * @date 2019/8/29
 */
public interface LogpoliceProperties {

    /**
     * 获取工程名
     */
    String getAppCode();

    /**
     * 获取工程地址
     */
    String getLocalIp();

    /**
     * 日志报警是否打开
     */
    Boolean getEnabled();

    /**
     * 日志报警清除时间
     */
    default Long getCleanTimeInterval() {
        return LogpoliceConstant.CLEAN_TIME_INTERVAL;
    }

    /**
     * 通知频率类型：按时间或按次数
     */
    default NoticeFrequencyType getFrequencyType() {
        return NoticeFrequencyType.TIMEOUT;
    }

    /**
     * 此次出现相同的异常时，与上次通知的时间做对比，假如超过此设定的值，则再次通知
     */
    default Long getTimeInterval() {
        return LogpoliceConstant.TIME_INTERVAL;
    }

    /**
     * 此次出现相同异常时，与上次通知的出现次数作对比，假如超过此设定的值，则再次通知
     */
    default Long getShowCount() {
        return LogpoliceConstant.SHOW_COUNT;
    }

    /**
     * 钉钉推送类型
     */
    default NoticeSendEnum getNoticeSendType() {
        return NoticeSendEnum.DING_DING;
    }

    /**
     * 日志报警是否打开redis
     */
    default Boolean getEnableRedisStorage() {
        return LogpoliceConstant.ENABLE_REDIS_STORAGE;
    }

    /**
     * 异常redisKey
     */
    default String getExceptionRedisKey() {
        return LogpoliceConstant.EXCEPTION_STATISTIC_REDIS_KEY;
    }

    /**
     * 异常白名单
     */
    default Set<String> getExceptionWhiteList() {
        return new HashSet<>();
    }
}
