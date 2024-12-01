package com.example.tools.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.Instant;

@Data
public class Log {
    @Null(groups = {Insert.class})
    @NotNull(groups = {Update.class}, message="id不能为空")
    private Long id;
    /**
     * 日志级别，Trace 0,
     *         Debug 1,
     *         Info 2,
     *         Warn 3,
     *         Error 4,
     *         Fatal 5
     */
    private Integer level;
    /**
     * 日志类型，
     *         Login 0,
     *         Auth 1,
     *         System 2,
     *         Business 3,
     *         Exception 4
     */
    private Integer logType;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 类名
     */
    private String className;
    /**
     * 浏览器信息
     */
    private String browserType;
    /**
     * 服务ip
     */
    private String serviceIp;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 请求参数
     */
    private String requestParam;
    /**
     * 响应结果
     */
    private String response;
    /**
     * 异常信息
     */
    private String exception;
    private String userName;
    private String userIp;
    private String roleName;
    /**
     * 请求开始时间
     */
    private Instant startTime;
    /**
     * 请求结束时间
     */
    private Instant endTime;
    /**
     * 请求持续时间
     */
    private Long elapsedTime;
}
