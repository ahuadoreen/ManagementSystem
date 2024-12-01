package com.example.ms.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.tools.component.IdentityUtils;
import com.example.tools.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WebIdentityUtils {
    @Autowired
    IdentityUtils identityUtils;

    public <T> void setCreateFields(T t) {
        String username = Objects.requireNonNull(WebUtils.getRequest()).getHeader(Constant.SECURITY_HEADER_USERNAME);
        identityUtils.setCreateFields(t, username);
    }

    public String getCurrentUserTrueName() {
        String username = Objects.requireNonNull(WebUtils.getRequest()).getHeader(Constant.SECURITY_HEADER_USERNAME);
        return identityUtils.getCurrentUserTrueName(username);
    }

    public <T> void setUpdateFields(T t) {
        String username = Objects.requireNonNull(WebUtils.getRequest()).getHeader(Constant.SECURITY_HEADER_USERNAME);
        identityUtils.setUpdateFields(t, username);
    }

    public JSONObject getCurrentUser() {
        String username = Objects.requireNonNull(WebUtils.getRequest()).getHeader(Constant.SECURITY_HEADER_USERNAME);
        return identityUtils.getCurrentUser(username);
    }

    public String getCurrentUsername() {
        return Objects.requireNonNull(WebUtils.getRequest()).getHeader(Constant.SECURITY_HEADER_USERNAME);
    }
}
