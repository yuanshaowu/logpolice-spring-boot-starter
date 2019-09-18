package com.logpolice.infrastructure.properties;

import com.logpolice.domain.entity.NoticeFrequencyType;
import com.logpolice.infrastructure.enums.NoticeSendEnum;

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
    Long getCleanTimeInterval();

    /**
     * 通知频率类型：按时间或按次数
     */
    NoticeFrequencyType getFrequencyType();

    /**
     * 此次出现相同的异常时，与上次通知的时间做对比，假如超过此设定的值，则再次通知
     */
    Long getTimeInterval();

    /**
     * 此次出现相同异常时，与上次通知的出现次数作对比，假如超过此设定的值，则再次通知
     */
    Long getShowCount();

    /**
     * 钉钉推送类型
     */
    NoticeSendEnum getNoticeSendType();

    /**
     * 日志报警是否打开redis
     */
    Boolean getEnableRedisStorage();

    /**
     * 异常redisKey
     */
    String getExceptionRedisKey();

    /**
     * 异常白名单
     */
    Set<String> getExceptionWhiteList();
}
