package com.logpolice.infrastructure.rpc;

import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.infrastructure.exception.EmailFormatException;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import com.logpolice.infrastructure.properties.LogpoliceMailProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 邮件推送逻辑层
 *
 * @author huang
 * @date 2019/8/28
 */
@Slf4j
public class MailNoticeClient implements ExceptionNoticeRepository {

    private final MailSender mailSender;
    private final MailProperties mailProperties;
    private final LogpoliceMailProperties logpoliceMailProperties;

    public MailNoticeClient(MailSender mailSender,
                            MailProperties mailProperties,
                            LogpoliceMailProperties logpoliceMailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.logpoliceMailProperties = logpoliceMailProperties;
    }

    @Override
    public void send(ExceptionNotice exceptionNotice) {
        checkAllEmails(logpoliceMailProperties);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String fromEmail = logpoliceMailProperties.getFrom();
        fromEmail = fromEmail == null ? mailProperties.getUsername() : fromEmail;
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(logpoliceMailProperties.getTo());
        String[] cc = logpoliceMailProperties.getCc();
        if (cc != null && cc.length > 0) {
            mailMessage.setCc(cc);
        }
        String[] bcc = logpoliceMailProperties.getBcc();
        if (bcc != null && bcc.length > 0) {
            mailMessage.setBcc(bcc);
        }
        mailMessage.setText(exceptionNotice.getText());
        mailMessage.setSubject(String.format("来自%s的异常提醒", exceptionNotice.getProject()));
        mailSender.send(mailMessage);
    }

    private boolean isEmail(String email) {
        return Strings.isNotEmpty(email) && Pattern.matches(LogpoliceConstant.MAIL_PATTERN_MATCHES, email);
    }

    private void checkAllEmails(LogpoliceMailProperties logpoliceMailProperties) {
        String fromEmail = logpoliceMailProperties.getFrom();
        if (Strings.isEmpty(fromEmail) || !isEmail(logpoliceMailProperties.getFrom())) {
            throw new EmailFormatException("mailNoticeClient.checkAllEmails fromEmail format error!");
        }
        String[] toEmail = logpoliceMailProperties.getTo();
        if (Objects.isNull(toEmail) || toEmail.length == 0 || Arrays.stream(toEmail).anyMatch(t -> !isEmail(t))) {
            throw new EmailFormatException("mailNoticeClient.checkAllEmails toEmail format error!");
        }
        String[] ccEmail = logpoliceMailProperties.getCc();
        if (Objects.nonNull(ccEmail) && Arrays.stream(ccEmail).anyMatch(c -> !isEmail(c))) {
            throw new EmailFormatException("mailNoticeClient.checkAllEmails ccEmail format error!");
        }
        String[] bccEmail = logpoliceMailProperties.getBcc();
        if (Objects.nonNull(bccEmail) && Arrays.stream(bccEmail).anyMatch(b -> !isEmail(b))) {
            throw new EmailFormatException("mailNoticeClient.checkAllEmails bccEmail format error!");
        }
    }
}
