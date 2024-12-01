package com.example.tools.entity;

public class CustomException extends RuntimeException {
    private final int code; //错误代码

    public CustomException(String message) {
        super(message);
        this.code = 1001;
    }

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
