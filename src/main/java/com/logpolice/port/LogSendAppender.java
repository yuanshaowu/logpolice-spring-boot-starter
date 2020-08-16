package com.logpolice.port;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.logpolice.application.NoticeService;
import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
import com.logpolice.infrastructure.utils.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 日志报警Appender
 *
 * @author huang
 * @date 2019/8/27
 */
@Slf4j
public class LogSendAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private PatternLayout layout;

    @Override
    public void start() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(this.context);
        patternLayout.setPattern(LogpoliceConstant.PROFILES_ACTIVE);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (Level.ERROR.equals(eventObject.getLevel())) {
            LogpoliceProperties logpoliceProperties = ApplicationContextProvider.getBean(LogpoliceProperties.class);
            if (!logpoliceProperties.getEnabled()) {
                return;
            }

            String logPattern = logpoliceProperties.getLogPattern();
            if (!Objects.equals(logpoliceProperties.getLogPattern(), layout.getPattern()) && !StringUtils.isEmpty(logPattern) && !StringUtils.isEmpty(logPattern.trim())) {
                PatternLayout patternLayout = new PatternLayout();
                patternLayout.setContext(this.context);
                patternLayout.setPattern(logPattern);
                patternLayout.start();
                this.layout = patternLayout;
                super.start();
            }

            String traceInfo;
            try {
                traceInfo = layout.doLayout(eventObject);
            } catch (Exception e) {
                log.warn("LogSendAppender.append getTraceInfo error!");
                return;
            }

            NoticeService noticeService = ApplicationContextProvider.getBean(NoticeService.class);
            ExceptionNotice exceptionNotice = new ExceptionNotice(logpoliceProperties.getAppCode(),
                    logpoliceProperties.getLocalIp(),
                    traceInfo,
                    eventObject,
                    logpoliceProperties.getExceptionRedisKey(),
                    !Objects.equals(LogpoliceConstant.PROFILES_ACTIVE, layout.getPattern()));
            noticeService.send(exceptionNotice, logpoliceProperties);
        }
    }

}