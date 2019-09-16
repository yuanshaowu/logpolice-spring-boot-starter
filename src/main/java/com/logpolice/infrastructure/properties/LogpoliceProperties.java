package com.logpolice.infrastructure.properties;

import com.logpolice.domain.entity.NoticeFrequencyType;
import com.logpolice.infrastructure.enums.NoticeSendEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志报警配置
 *
 * @author huang
 * @date 2019/8/29
 */
@ConfigurationProperties(prefix = "logpolice")
public class LogpoliceProperties {

    /**
     * 获取工程名
     */
    @Getter
    @Setter
    private String appCode = LogpoliceConstant.APP_CODE;

    /**
     * 获取工程地址
     */
    @Getter
    @Setter
    private String localIp = LogpoliceConstant.LOCAL_IP;

    /**
     * 日志报警是否打开
     */
    @Getter
    @Setter
    private Boolean enabled = LogpoliceConstant.ENABLED;

    /**
     * 日志报警清除时间
     */
    @Getter
    @Setter
    private Long cleanTimeInterval = LogpoliceConstant.CLEAN_TIME_INTERVAL;

    /**
     * 通知频率类型：按时间或按次数
     */
    @Getter
    @Setter
    private NoticeFrequencyType frequencyType = NoticeFrequencyType.TIMEOUT;

    /**
     * 此次出现相同的异常时，与上次通知的时间做对比，假如超过此设定的值，则再次通知
     */
    @Getter
    @Setter
    private Long timeInterval = LogpoliceConstant.TIME_INTERVAL;

    /**
     * 此次出现相同异常时，与上次通知的出现次数作对比，假如超过此设定的值，则再次通知
     */
    @Getter
    @Setter
    private Long showCount = LogpoliceConstant.SHOW_COUNT;

    /**
     * 钉钉推送类型
     */
    @Getter
    @Setter
    private NoticeSendEnum noticeSendType = NoticeSendEnum.DING_DING;

    /**
     * 日志报警是否打开redis
     */
    @Getter
    @Setter
    private Boolean enableRedisStorage = LogpoliceConstant.ENABLE_REDIS_STORAGE;
}
