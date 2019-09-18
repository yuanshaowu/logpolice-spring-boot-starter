package com.logpolice.infrastructure.properties;

/**
 * 日志报警邮件配置
 *
 * @author huang
 * @date 2019/8/29
 */
public interface LogpoliceMailProperties {

    /**
     * 发件人，默认是通过springboot javamail配置的stmp的用户名
     */
    String getFrom();

    /**
     * 收件人
     */

    String[] getTo();

    /**
     * 抄送
     */

    String[] getCc();

    /**
     * 密抄送
     */

    String[] getBcc();
}
