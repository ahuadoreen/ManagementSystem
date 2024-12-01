package com.example.tools.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseData<T> {
    @Schema(example = "OK")
    private String message;
    @Schema(example = "200")
    private int code;
    private T data;
    @JSONField(serialize = false)
    private String exception;

    public void ok() {
        ok("OK", null);
    }

    public void unauthorized() {
        setCode(401);
        setMessage("Unauthorized user");
    }

    public void forbidden() {
        setCode(403);
        setMessage("No authorization to access");
    }

    public void internalError() {
        setCode(500);
        setMessage("Internal error");
    }

    public void badRequest() {
        setCode(400);
        setMessage("Request type or parameter error");
    }

    public void ok(String message, T data) {
        setCode(200);
        setMessage(message);
        setData(data);
    }

    public void ok(T data) {
        ok("OK", data);
    }

    public void error(int code, String message) {
        setCode(1001);
        setMessage(message);
    }

    public void customError(String message) {
        setCode(1001);
        setMessage(message);
    }
}
