package com.logpolice.infrastructure.dto;

import lombok.*;

/**
 * 飞书命令
 *
 * @author huang
 * @date 2019/8/27
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuCommand {

    /**
     * 标题
     */
    @Getter
    @Setter
    private String title;

    /**
     * 文本命令
     */
    @Getter
    @Setter
    private String text;
}
