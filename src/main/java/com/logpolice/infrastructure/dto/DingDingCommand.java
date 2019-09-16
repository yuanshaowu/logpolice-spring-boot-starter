package com.logpolice.infrastructure.dto;

import lombok.*;

import java.util.Set;

/**
 * 钉钉命令
 *
 * @author huang
 * @date 2019/8/27
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DingDingCommand {

    /**
     * 消息内容
     */
    @Getter
    @Setter
    private String msgtype;

    /**
     * 钉钉文本命令
     */
    @Getter
    @Setter
    private DingDingTextCommand text;

    /**
     * 钉钉对象命令
     */
    @Getter
    @Setter
    private DingDingAtCommand at;

    /**
     * 创建钉钉命令（自定义构造）
     *
     * @param content   消息内容
     * @param msgtype   此消息类型为固定text
     * @param atMobiles 被@人的手机号
     * @param isAtAll   所有人@时：true，否则为false
     */
    public DingDingCommand(String content, String msgtype, Set<String> atMobiles, Boolean isAtAll) {
        this.msgtype = msgtype;
        this.at = new DingDingAtCommand(atMobiles, isAtAll);
        this.text = new DingDingTextCommand(content);
    }
}
