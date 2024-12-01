package com.example.ms.gateway.component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;

/**
 * 拦截swagger文档的路径，把返回的直接的服务地址改为通过网关访问的地址
 */
@Component
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
public class ApiDocPathFilter implements GlobalFilter, Ordered {
    @Autowired
    private SpringDocConfigProperties springDocConfigProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //直接用配置类的值，默认值是 /v3/api-docs
        String apiPath = springDocConfigProperties.getApiDocs().getPath();
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        ServerHttpResponse response = exchange.getResponse();

        //非正常状态跳过
        if (Objects.requireNonNull(response.getStatusCode()).value() != HttpStatus.OK.value()) {
            return chain.filter(exchange);
        }

        //非springdoc文档不拦截
        if (!(StringUtils.isNotBlank(uri.getPath()) && uri.getPath().endsWith(apiPath))) {
            return chain.filter(exchange);
        }

        String uriString = uri.toString();
        String gatewayUrl = uriString.substring(0, uriString.lastIndexOf(apiPath));
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
                        String bodyString = new String(content);
                        JSONObject jsonObject = JSONObject.parseObject(bodyString);
                        JSONArray servers = jsonObject.getJSONArray("servers");
                        if (servers != null && !servers.isEmpty()) {
                            JSONObject server = servers.getJSONObject(0);
                            server.put("url", gatewayUrl);
                        }
                        byte[] responseBytes = JSON.toJSONBytes(jsonObject);
                        response.getHeaders().setContentLength(responseBytes.length);
                        return response.bufferFactory().wrap(responseBytes);
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
        return -2;
    }
}
