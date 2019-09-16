package com.logpolice.infrastructure.dto;

import lombok.*;

import java.util.Set;

/**
 * 钉钉对象命令
 *
 * @author huang
 * @date 2019/8/28
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DingDingAtCommand {

    /**
     * 被@人的手机号
     */
    @Getter
    @Setter
    private Set<String> atMobiles;

    /**
     * 所有人@时：true，否则为false
     */
    @Getter
    @Setter
    private boolean isAtAll;
}