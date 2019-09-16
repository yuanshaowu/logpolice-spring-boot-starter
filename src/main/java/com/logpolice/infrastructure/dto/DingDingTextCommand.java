package com.logpolice.infrastructure.dto;

import lombok.*;

/**
 * 钉钉文本命令
 *
 * @author huang
 * @date 2019/8/28
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DingDingTextCommand {

    /**
     * 此消息类型为固定text
     */
    @Getter
    @Setter
    private String content;
}