package com.logpolice.domain.entity;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.logpolice.infrastructure.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
     * 堆栈信息
     */
    @Transient
    private List<String> stackTraceElementProxys;

    /**
     * 格式化时间
     */
    private final static String DEFAULT_INFO = "未输出堆栈异常信息";

    /**
     * 异常追踪信息
     */
    private final static String SIMPLIFY_TRACE_INFO = "...... not first push will show simplify frames omitted";

    /**
     * 异常追踪信息最小行数
     */
    private final static int TRACE_MIN_NUM = 3;

    /**
     * 换行符
     */
    private final static String LINE = "\n";

    /**
     * 创建异常信息（自定义构造）
     *
     * @param project        工程
     * @param projectAddress 工程地址
     * @param traceInfo      堆栈
     * @param eventObject    事件对象
     * @param cacheKey       缓存key
     */
    public ExceptionNotice(String project, String projectAddress, String traceInfo, LoggingEvent eventObject, String cacheKey) {
        this.project = project;
        this.projectAddress = projectAddress;
        this.classPath = eventObject.getLoggerName();
        this.params = eventObject.getArgumentArray();
        this.exceptionMessage = eventObject.getFormattedMessage();
        this.traceInfo = StringUtils.isEmpty(traceInfo) ? DEFAULT_INFO : traceInfo;
        LocalDateTime localDateTime = DateUtils.getLocalDateTime(eventObject.getTimeStamp());
        this.latestShowTime = localDateTime;
        this.firstShowTime = localDateTime;
        this.showCount = 1L;

        IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
        if (Objects.nonNull(throwableProxy)) {
            updateMethodName(throwableProxy);
            updateStackTraceElementProxys(throwableProxy);
            this.exceptionClassName = throwableProxy.getClassName();
        }
        this.openId = calOpenId(cacheKey);
    }

    /**
     * 更新方法名
     *
     * @param throwableProxy 异常代理
     */
    private void updateMethodName(IThrowableProxy throwableProxy) {
        Arrays.stream(throwableProxy.getStackTraceElementProxyArray())
                .filter(s -> s.getStackTraceElement().getClassName().contains(classPath)
                        && Objects.nonNull(s.getStackTraceElement().getFileName()))
                .findFirst()
                .ifPresent(s -> this.methodName = s.getStackTraceElement().getMethodName());
    }

    /**
     * 设置异常堆栈信息
     *
     * @param throwableProxy 异常代理
     */
    private void updateStackTraceElementProxys(IThrowableProxy throwableProxy) {
        String firstThrowable = throwableProxy.getClassName() + "：" + throwableProxy.getMessage();
        List<String> stackTraceStr = Arrays.stream(throwableProxy.getStackTraceElementProxyArray())
                .map(StackTraceElementProxy::getSTEAsString)
                .collect(Collectors.toList());

        List<String> stackTraces = new ArrayList<>();
        stackTraces.add(firstThrowable);
        stackTraces.addAll(stackTraceStr);
        this.stackTraceElementProxys = stackTraces;
    }

    /**
     * MD5异常key
     *
     * @param cacheKey 缓存key
     * @return key
     */
    private String calOpenId(String cacheKey) {
        String openIdStr = String.format("%s-%s-%s-%s", classPath, methodName, exceptionClassName, cacheKey);
        return DigestUtils.md5DigestAsHex(openIdStr.getBytes());
    }

    /**
     * 更新数据
     *
     * @param showCount      出现次数
     * @param latestShowTime 最后一次出现的时间
     * @param firstShowTime  日志保留时间
     */
    public void updateData(Integer showCount, LocalDateTime latestShowTime, LocalDateTime firstShowTime) {
        this.showCount = Math.max(this.showCount, showCount);
        this.latestShowTime = latestShowTime;
        this.firstShowTime = isFirst() ? latestShowTime : firstShowTime;
    }

    /**
     * 获取文本
     *
     * @return 文本
     */
    public String getText() {
        StringBuilder text = new StringBuilder();
        text.append("工程名：").append(project).append(LINE)
                .append("工程地址：").append(StringUtils.isEmpty(projectAddress) ? "获取失败" : projectAddress).append(LINE)
                .append("类路径：").append(classPath).append(LINE)
                .append("方法名：").append(methodName).append(LINE)
                .append("参数信息：").append(Objects.isNull(params) ? "无" : Arrays.toString(params)).append(LINE)
                .append("异常信息：").append(exceptionMessage).append(LINE)
                .append("异常追踪：").append(isFirst() ? getDetailTraceInfo() : getSimplifyTraceInfo()).append(LINE)
                .append("首次时间：").append(DateUtils.format(firstShowTime)).append(LINE)
                .append("最近时间：").append(DateUtils.format(latestShowTime)).append(LINE)
                .append("异常次数：").append(showCount);
        return text.toString();
    }

    /**
     * 获取简化堆栈信息
     *
     * @return 堆栈信息
     */
    public String getSimplifyTraceInfo() {
        if (CollectionUtils.isEmpty(stackTraceElementProxys) || stackTraceElementProxys.size() < TRACE_MIN_NUM) {
            return traceInfo;
        }

        List<String> simplifyTraceInfos = stackTraceElementProxys.stream()
                .limit(Math.min(TRACE_MIN_NUM, stackTraceElementProxys.size()))
                .collect(Collectors.toList());
        simplifyTraceInfos.add(SIMPLIFY_TRACE_INFO);
        return simplifyTraceInfos.stream().collect(Collectors.joining(LINE));
    }

    /**
     * 获取详细堆栈信息
     *
     * @return 堆栈信息
     */
    public String getDetailTraceInfo() {
        if (CollectionUtils.isEmpty(stackTraceElementProxys)) {
            return traceInfo;
        }
        return stackTraceElementProxys.stream().collect(Collectors.joining(LINE));
    }

    /**
     * 是否首次推送
     *
     * @return 是否首次
     */
    public boolean isFirst() {
        return Objects.equals(showCount, 1L);
    }

    /**
     * 判断是否符合白名单
     *
     * @param exceptionWhiteList 异常白名单
     * @param classWhiteList     类白名单
     * @return 布尔
     */
    public boolean containsWhiteList(Set<String> exceptionWhiteList, Set<String> classWhiteList) {
        if (!CollectionUtils.isEmpty(exceptionWhiteList) && !StringUtils.isEmpty(exceptionClassName)) {
            return exceptionWhiteList.contains(exceptionClassName);
        }
        if (!CollectionUtils.isEmpty(classWhiteList) && !StringUtils.isEmpty(classPath)) {
            return classWhiteList.contains(classPath);
        }
        return false;
    }
}
