package com.logpolice.infrastructure.exception;

/**
 * 邮箱格式异常
 *
 * @author huang
 * @date 2019/8/29
 */
public class EmailFormatException extends RuntimeException {

    public EmailFormatException(String message, Object... args) {
        super(String.format(message, args));
    }
}
