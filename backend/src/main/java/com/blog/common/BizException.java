package com.blog.common;

/**
 * 业务异常：业务规则校验失败时抛出，由 GlobalExceptionHandler 统一转成
 * { "error": "..." } 响应，替代散落在 Controller 的手写 ResponseEntity。
 */
public class BizException extends RuntimeException {

    private final ErrorCode code;

    public BizException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }

    public static BizException badRequest(String message) {
        return new BizException(ErrorCode.BAD_REQUEST, message);
    }

    public static BizException unauthorized(String message) {
        return new BizException(ErrorCode.UNAUTHORIZED, message);
    }

    public static BizException notFound(String message) {
        return new BizException(ErrorCode.NOT_FOUND, message);
    }

    public static BizException conflict(String message) {
        return new BizException(ErrorCode.CONFLICT, message);
    }

    public static BizException internalError(String message) {
        return new BizException(ErrorCode.INTERNAL_ERROR, message);
    }
}
