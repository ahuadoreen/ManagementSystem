package com.example.tools.component;

import com.alibaba.fastjson2.JSONObject;
import com.example.tools.entity.Log;
import com.example.tools.entity.ResponseData;
import com.example.tools.utils.CommonUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LogUtil {
    @Autowired
    LoadBalancerClient loadBalancerClient;
    public enum LogLevel {
        Trace,
        Debug,
        Info,
        Warn,
        Error,
        Fatal
    }

    public enum LogType {
        Login,
        Auth,
        System,
        Business,
        Exception
    }

    private ConcurrentLinkedQueue<Log> logQueue = new ConcurrentLinkedQueue<>();
    private LogUtil() {
        System.out.println("日志工具初始化");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(this::start);
    }
    private void addLog(LogLevel logLevel, LogType logType, String serviceName, String className,
                        String requestParam, String response, String exception, String serviceIp,
                        String userIp, String userName, String roleName, String url, String browserType,
                        Instant startTime) {
        Log log = new Log();
        log.setLevel(logLevel.ordinal());
        log.setLogType(logType.ordinal());
        log.setServiceName(serviceName);
        log.setClassName(className);
        log.setRequestParam(requestParam);
        log.setResponse(response);
        log.setException(exception);
        log.setServiceIp(serviceIp);
        log.setUserIp(userIp);
        log.setUserName(userName);
        log.setRoleName(roleName);
        log.setUrl(url);
        log.setBrowserType(browserType);
        log.setStartTime(startTime);
        Instant endTime = Instant.now();
        log.setEndTime(endTime);
        Duration duration = Duration.between(startTime, endTime);
        log.setElapsedTime(duration.toMillis());
        logQueue.add(log);
    }

    public void addLog(String className, String response, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        LogLevel logLevel = LogLevel.Info;
        LogType logType = LogType.System;
        if (url.contains("login")) {
            logType = LogType.Login;
        }
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.get("exception") != null) {
            logType = LogType.Exception;
            logLevel = LogLevel.Error;
        }
        String[] urlSplit = url.split("/");
        String serviceName = null;
        if (urlSplit.length > 2) {
            serviceName = urlSplit[1];
        }
        addLog(logLevel, logType, serviceName, className, null, response, (String) jsonObject.get("exception"), exchange);
    }

    public void addLog(LogLevel logLevel, LogType logType, String serviceName, String className,
                        String requestParam, String response, String exception, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
//        System.out.println(CommonUtils.getMacAddress(request));
        String url = request.getURI().getPath();
        HttpHeaders headers = request.getHeaders();
        String userName = headers.getFirst("username");
        if (StringUtils.isBlank(userName)) {
            userName = "Anonymous";
        }
        if (StringUtils.isBlank(requestParam)) {
            requestParam = exchange.getAttribute("cachedRequestBody");
        }
        addLog(logLevel, logType, serviceName, className, requestParam, response, exception,
                CommonUtils.getIp(request),
                Objects.requireNonNull(request.getLocalAddress()).toString(),
                userName,
                null,
                url,
                headers.getFirst("User-Agent"),
                Instant.parse(headers.getFirst("startTime")));
    }

    private void start() {
        List<Log> logList = new ArrayList<>();
        while (true) {
            try {
                if (!logQueue.isEmpty() && logList.size() < 500) {
                    logList.addAll(logQueue);
                    logQueue.clear();
                    continue;
                }
                //每5秒写一次数据
                Thread.sleep(5000);
                if (logList.isEmpty()) { continue; }
                ServiceInstance instance = loadBalancerClient.choose("basic-module");
                String uri = instance.getUri().toString();
                ResponseData responseData = WebClient.create().post()
                        .uri(uri + "/log/addMultiple")
                        .bodyValue(logList)
                        .retrieve()
                        .bodyToMono(ResponseData.class).block();
                if (responseData.getCode() == 200) {
                    logList.clear();
                } else {
                    log.error("日志写入数据库失败", responseData.getMessage());
                }
            } catch (Exception e) {
                log.error("日志写入数据库失败", e);
            }
        }
    }
}
