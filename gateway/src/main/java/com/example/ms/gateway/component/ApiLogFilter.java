package com.example.ms.gateway.component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.tools.component.LogUtil;
import com.example.tools.entity.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;

/**
 * 拦截请求，把请求信息记录到日志中
 */
@Component
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
public class ApiLogFilter implements GlobalFilter, Ordered {
    @Autowired
    private SpringDocConfigProperties springDocConfigProperties;

    @Autowired
    LogUtil logUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String apiPath = springDocConfigProperties.getApiDocs().getPath();
        ServerHttpRequest request = exchange.getRequest();
        // get请求不记录
        if (request.getMethod() == HttpMethod.GET) {
            return chain.filter(exchange);
        }
        // swagger文档路径跳过
        URI uri = request.getURI();
        if (StringUtils.isNotBlank(uri.getPath()) && (uri.getPath().endsWith(apiPath))) {
            return chain.filter(exchange);
        }
        ServerHttpResponse response = exchange.getResponse();
        // 可能是查询的请求成功的不记录
        if ((uri.getPath().contains("search") || uri.getPath().contains("get") || uri.getPath().contains("export") || uri.getPath().contains("download"))
            && Objects.requireNonNull(response.getStatusCode()).value() == HttpStatus.OK.value()) {
            return chain.filter(exchange);
        }
        request.mutate().header("startTime", Instant.now().toString()).build();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux<? extends DataBuffer> fluxBody) {
                    return super.writeWith(fluxBody.buffer().map(dataBuffer -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffer);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        //释放掉内存
                        DataBufferUtils.release(join);
                        String bodyString = new String(content, StandardCharsets.UTF_8);
                        logUtil.addLog(getClass().getName(), bodyString, exchange);
                        // 重新反序列化和序列化一下是为了隐藏放在responseData中的exception信息，如果不担心这个问题，也可以不用这样处理，直接返回原content
                        ResponseData responseData = JSONObject.parseObject(bodyString, ResponseData.class);
                        return response.bufferFactory().wrap(JSON.toJSONBytes(responseData));
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
