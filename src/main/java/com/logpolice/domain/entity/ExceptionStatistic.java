package com.logpolice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 异常信息统计
 *
 * @author huang
 * @date 2019/8/28
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionStatistic implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    @Getter
    private String openId;

    /**
     * 异常出现次数
     */
    @Getter
    private Integer showCount;

    /**
     * 上一次通知时的次数
     */
    @Getter
    private Integer lastShowedCount;

    /**
     * 首次异常时间
     */
    @Getter
    private LocalDateTime firstTime;

    /**
     * 异常通知的时间
     */
    @Getter
    private LocalDateTime noticeTime;

    /**
     * 创建异常统计
     *
     * @param openId 唯一标识
     */
    public ExceptionStatistic(String openId) {
        this.showCount = 0;
        this.lastShowedCount = 0;
        this.openId = openId;
        LocalDateTime now = LocalDateTime.now();
        this.firstTime = now;
        this.noticeTime = now;
    }

    /**
     * 重置数据
     */
    public void reset() {
        this.showCount = 0;
        this.lastShowedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        this.firstTime = now;
        this.noticeTime = now;
    }

    /**
     * 是否首次推送
     *
     * @return 布尔
     */
    public boolean isFirst() {
        return Objects.equals(showCount.longValue(), 1L);
    }

    /**
     * 追加统计次数
     */
    public void pushOne() {
        showCount += 1;
    }

    /**
     * 更新数据
     */
    public void updateData() {
        this.lastShowedCount = showCount;
        this.noticeTime = LocalDateTime.now();
    }

    /**
     * 是否超时过期
     *
     * @param cleanTimeInterval 超时时间
     * @return 过期
     */
    public Boolean isTimeOut(Long cleanTimeInterval) {
        Duration dur = Duration.between(firstTime, LocalDateTime.now());
        return Duration.ofSeconds(cleanTimeInterval).compareTo(dur) < 0;
    }

    /**
     * 更新版本号
     *
     * @return 版本号
     */
    public String getVersion() {
        String openIdStr = String.format("%s-%s-%s", openId, showCount, lastShowedCount);
        return DigestUtils.md5DigestAsHex(openIdStr.getBytes());
    }
}
