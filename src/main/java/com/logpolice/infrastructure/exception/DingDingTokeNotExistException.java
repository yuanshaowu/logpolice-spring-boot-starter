package com.logpolice.infrastructure.exception;

/**
 * 钉钉token空异常
 *
 * @author huang
 * @date 2019/8/29
 */
public class DingDingTokeNotExistException extends RuntimeException {

    public DingDingTokeNotExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
