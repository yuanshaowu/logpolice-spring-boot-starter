package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.properties.LogpoliceMailProperties;
import com.logpolice.infrastructure.rpc.MailNoticeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

/**
 * 邮件配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({MailSenderAutoConfiguration.class, LogpoliceAutoConfiguration.class})
@ConditionalOnBean({MailSender.class, MailProperties.class})
@ConditionalOnProperty(name = "logpolice.notice-send-type", havingValue = "MAIL")
public class LogpoliceMailAutoConfiguration {

    private final MailSender mailSender;
    private final MailProperties mailProperties;
    private final LogpoliceMailProperties logpoliceMailProperties;

    @Autowired
    public LogpoliceMailAutoConfiguration(MailSender mailSender,
                                          MailProperties mailProperties,
                                          LogpoliceMailProperties logpoliceMailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.logpoliceMailProperties = logpoliceMailProperties;
    }

    @Bean
    public MailNoticeClient mailNoticeRpc() {
        return new MailNoticeClient(mailSender, mailProperties, logpoliceMailProperties);
    }
}
