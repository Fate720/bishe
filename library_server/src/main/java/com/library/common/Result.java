package com.library.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    /** 预定义错误码 */
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_SERVER_ERROR = 500;
    public static final int CODE_BORROW_LIMIT = 1001;
    public static final int CODE_BOOK_UNAVAILABLE = 1002;
    public static final int CODE_OVERDUE = 1003;

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(CODE_SUCCESS, "操作成功", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(CODE_SUCCESS, "操作成功", null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(CODE_SERVER_ERROR, message, null);
    }

    public static <T> Result<T> badRequest(String message) {
        return new Result<>(CODE_BAD_REQUEST, message, null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(CODE_UNAUTHORIZED, message, null);
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(CODE_FORBIDDEN, message, null);
    }

    public static <T> Result<T> notFound(String message) {
        return new Result<>(CODE_NOT_FOUND, message, null);
    }
}
