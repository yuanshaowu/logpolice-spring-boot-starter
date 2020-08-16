package com.logpolice.infrastructure.exception;

/**
 * 飞书token空异常
 *
 * @author huang
 * @date 2019/8/29
 */
public class FeiShuTokeNotExistException extends RuntimeException {

    public FeiShuTokeNotExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
