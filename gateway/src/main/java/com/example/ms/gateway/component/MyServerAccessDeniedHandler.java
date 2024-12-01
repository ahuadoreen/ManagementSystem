package com.example.ms.gateway.component;

import com.alibaba.fastjson2.JSON;
import com.example.tools.component.LogUtil;
import com.example.tools.entity.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class MyServerAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Autowired
    LogUtil logUtil;
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap((response) -> {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            ResponseData<String> responseData = new ResponseData<>();
            responseData.forbidden();
            logUtil.addLog(LogUtil.LogLevel.Error, LogUtil.LogType.Auth, "gateway", getClass().getName(), null, JSON.toJSONString(responseData), null, exchange);
            DataBuffer buffer = dataBufferFactory.wrap(JSON.toJSONBytes(responseData));
            return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
                DataBufferUtils.release(buffer);
            });
        });
    }
}
