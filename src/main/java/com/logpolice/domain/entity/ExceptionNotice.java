package com.logpolice.domain.entity;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.logpolice.infrastructure.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * 异常信息
 *
 * @author huang
 * @date 2019/8/28
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionNotice implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 工程名
     */
    @Getter
    private String project;

    /**
     * 工程地址
     */
    @Getter
    private String projectAddress;

    /**
     * 唯一标识
     */
    @Getter
    private String openId;

    /**
     * 方法名
     */
    @Getter
    private String methodName = DEFAULT_INFO;

    /**
     * 方法参数信息
     */
    @Getter
    private Object[] params;

    /**
     * 类路径
     */
    @Getter
    private String classPath;

    /**
     * 异常信息
     */
    @Getter
    private String exceptionMessage;

    /**
     * 异常类型
     */
    @Getter
    private String exceptionClassName = DEFAULT_INFO;

    /**
     * 异常追踪信息
     */
    @Getter
    private String traceInfo;

    /**
     * 最后一次出现的时间
     */
    @Getter
    private LocalDateTime latestShowTime;

    /**
     * 日志保留时间
     */
    @Getter
    private LocalDateTime firstShowTime;

    /**
     * 出现次数
     */
    @Getter
    private Long showCount;

    /**
     * 格式化时间
     */
    private final static String DEFAULT_INFO = "未输出堆栈异常信息";

    /**
     * 异常信息
     *
     * @param project        工程
     * @param projectAddress 工程地址
     * @param traceInfo      堆栈
     * @param eventObject    事件对象
     */
    public ExceptionNotice(String project, String projectAddress, String traceInfo, LoggingEvent eventObject) {
        this.project = project;
        this.projectAddress = projectAddress;
        this.classPath = eventObject.getLoggerName();
        this.params = eventObject.getArgumentArray();
        this.exceptionMessage = eventObject.getFormattedMessage();
        this.traceInfo = traceInfo;
        LocalDateTime localDateTime = DateUtils.getLocalDateTime(eventObject.getTimeStamp());
        this.latestShowTime = localDateTime;
        this.firstShowTime = localDateTime;
        this.showCount = 1L;
        if (Objects.nonNull(eventObject.getThrowableProxy())) {
            Arrays.stream(eventObject.getThrowableProxy().getStackTraceElementProxyArray())
                    .filter(s -> s.getStackTraceElement().getClassName().contains(classPath)
                            && Objects.nonNull(s.getStackTraceElement().getFileName())).findFirst()
                    .ifPresent(s -> this.methodName = s.getStackTraceElement().getMethodName());
            this.exceptionClassName = eventObject.getThrowableProxy().getClassName();
        }
        this.openId = calOpenId();
    }

    /**
     * MD5异常key
     *
     * @return key
     */
    private String calOpenId() {
        return DigestUtils.md5DigestAsHex(String.format("%s-%s-%s", classPath, methodName, exceptionClassName).getBytes());
    }

    /**
     * 更新数据
     *
     * @param showCount      出现次数
     * @param latestShowTime 最后一次出现的时间
     * @param firstShowTime  日志保留时间
     */
    public void updateData(Long showCount, LocalDateTime latestShowTime, LocalDateTime firstShowTime) {
        this.showCount = Math.max(this.showCount, showCount);
        this.latestShowTime = latestShowTime;
        if (Objects.equals(this.showCount, 1L)) {
            this.firstShowTime = latestShowTime;
        } else {
            this.firstShowTime = firstShowTime;
        }
    }

    /**
     * 获取文本
     *
     * @return 文本
     */
    public String getText() {
        StringBuilder text = new StringBuilder();
        text.append("工程名：").append(project).append("\r\n")
                .append("工程地址：").append(StringUtils.isEmpty(projectAddress) ? "获取失败" : projectAddress).append("\r\n")
                .append("类路径：").append(classPath).append("\r\n")
                .append("方法名：").append(methodName).append("\r\n")
                .append("参数信息：").append(Objects.isNull(params) ? "无" : Arrays.toString(params)).append("\r\n")
                .append("异常信息：").append(exceptionMessage).append("\r\n")
                .append("异常追踪：").append(traceInfo).append("\r\n")
                .append("首次时间：").append(DateUtils.format(firstShowTime)).append("\r\n")
                .append("最近时间：").append(DateUtils.format(latestShowTime)).append("\r\n")
                .append("异常次数：").append(showCount);
        return text.toString();
    }

    /**
     * 判断是否符合白名单
     *
     * @param exceptionWhiteList 异常白名单
     * @param classWhiteList     类白名单
     * @return 布尔
     */
    public boolean isSkip(Set<String> exceptionWhiteList, Set<String> classWhiteList) {
        if (!CollectionUtils.isEmpty(exceptionWhiteList) && !StringUtils.isEmpty(exceptionClassName)) {
            return exceptionWhiteList.contains(exceptionClassName);
        }
        if (!CollectionUtils.isEmpty(classWhiteList) && !StringUtils.isEmpty(classPath)) {
            return classWhiteList.contains(classPath);
        }
        return false;
    }
}
