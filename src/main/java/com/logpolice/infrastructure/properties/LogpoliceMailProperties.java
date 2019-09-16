package com.logpolice.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志报警邮件配置
 *
 * @author huang
 * @date 2019/8/29
 */
@ConfigurationProperties(prefix = "logpolice.mail")
public class LogpoliceMailProperties {

    /**
     * 发件人，默认是通过springboot javamail配置的stmp的用户名
     */
    @Getter
    @Setter
    private String from;

    /**
     * 收件人
     */
    @Getter
    @Setter
    private String[] to;

    /**
     * 抄送
     */
    @Getter
    @Setter
    private String[] cc;

    /**
     * 密抄送
     */
    @Getter
    @Setter
    private String[] bcc;
}
