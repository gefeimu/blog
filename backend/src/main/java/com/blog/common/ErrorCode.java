package com.blog.common;

import org.springframework.http.HttpStatus;

/**
 * 统一错误码：所有业务异常的状态码与语义在此收敛。
 */
public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "参数错误"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "未授权"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "资源不存在"),
    CONFLICT(HttpStatus.CONFLICT, "资源冲突"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
