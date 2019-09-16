package com.logpolice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

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
     * 异常出现次数
     */
    @Getter
    private AtomicLong showCount;

    /**
     * 唯一id
     */
    @Getter
    private String openId;

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
     * 上一次通知时的次数
     */
    @Getter
    private Long lastShowedCount;

    /**
     * 创建异常统计
     *
     * @param openId 唯一标识
     */
    public ExceptionStatistic(String openId) {
        this.showCount = new AtomicLong(0);
        this.lastShowedCount = 0L;
        this.openId = openId;
        LocalDateTime now = LocalDateTime.now();
        this.firstTime = now;
        this.noticeTime = now;
    }

    /**
     * 重置数据
     */
    public void resetData() {
        this.showCount = new AtomicLong(1);
        this.lastShowedCount = 1L;
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
     *
     * @return 追加统计次数
     */
    public Long pushOne() {
        return showCount.incrementAndGet();
    }

    /**
     * 更新数据
     *
     * @param lastShowedCount 上一次通知时的次数
     * @param noticeTime      异常通知的时间
     */
    public void updateData(Long lastShowedCount, LocalDateTime noticeTime) {
        this.lastShowedCount = lastShowedCount;
        this.noticeTime = noticeTime;
    }
}
