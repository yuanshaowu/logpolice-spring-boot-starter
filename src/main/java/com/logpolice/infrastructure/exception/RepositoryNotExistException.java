package com.logpolice.infrastructure.exception;

/**
 * 仓储空异常
 *
 * @author huang
 * @date 2019/8/29
 */
public class RepositoryNotExistException extends RuntimeException {

    public RepositoryNotExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
