package com.example.tools.component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.tools.entity.ResponseData;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;

@Component
public class IdentityService {
    WebClient webClient = WebClient.create();
    @Autowired
    LoadBalancerClient loadBalancerClient;

    public JSONObject getCurrentUser(String username) {
        ServiceInstance instance = loadBalancerClient.choose("security-module");
        String uri = instance.getUri().toString();
        ResponseData responseData = webClient.get()
                .uri(uri + "/user/getUserByUsername?username=" + username) // 替换为你的服务ID和端点
                .retrieve()
                .bodyToMono(ResponseData.class).block();
        HashMap<String, Object> data = (HashMap<String, Object>) responseData.getData();
        return JSONObject.parseObject(JSON.toJSONString(data));
    }
}
