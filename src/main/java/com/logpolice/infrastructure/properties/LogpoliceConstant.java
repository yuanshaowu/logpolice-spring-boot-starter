package com.logpolice.infrastructure.properties;

/**
 * 日志报警配置
 *
 * @author huang
 * @date 2019/8/29
 */
public class LogpoliceConstant {

    /**
     * 默认工程名
     */
    public static final String APP_CODE = "暂无获取";

    /**
     * 默认工程地址
     */
    public static final String LOCAL_IP = "暂无获取";

    /**
     * 默认日志报警开关
     */
    public static final Boolean ENABLED = false;

    /**
     * 默认日志redis开关
     */
    public static final Boolean ENABLE_REDIS_STORAGE = false;

    /**
     * 默认日志报警清除时间
     */
    public static final Long CLEAN_TIME_INTERVAL = 10800L;

    /**
     * 默认异常间隔时间
     */
    public static final Long TIME_INTERVAL = 300L;

    /**
     * 默认异常间隔次数
     */
    public static final Long SHOW_COUNT = 10L;

    /**
     * 钉钉默认文本
     */
    public static final String DING_DING_TEXT = "text";

    /**
     * 异常追踪头
     */
    public static final String PROFILES_ACTIVE = " ";

    /**
     * 钉钉所有人@时：true，否则为false
     */
    public static final Boolean DING_DING_IS_AT_ALL = false;

    /**
     * 钉钉所有人@时：true，否则为false
     */
    public static final String EXCEPTION_STATISTIC_REDIS_KEY = "logpolice_exception_statistic:";

    /**
     * 邮件规则匹配
     */
    public static final String MAIL_PATTERN_MATCHES = "^[A-Za-z0-9_\\-]+@[a-zA-Z0-9_\\-]+(\\.[a-zA-Z]{2,4})+$";
}
