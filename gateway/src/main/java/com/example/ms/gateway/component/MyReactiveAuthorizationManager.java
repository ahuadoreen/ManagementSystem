package com.example.ms.gateway.component;

import com.example.tools.utils.Constant;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class MyReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private String authority;

    public MyReactiveAuthorizationManager(String authority) {
        this.authority = authority;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(auth->{
                    if (auth.equals(Constant.ROLE_SUPER_ADMIN)) {
                        return true;
                    }
                    String uri = context.getExchange().getRequest().getPath().value();
                    String[] uris = uri.split("/");
                    if (this.authority == null) {
                        this.authority = uris[uris.length - 1];
                    }
                    String authority = uris[uris.length - 3] + "." + uris[uris.length - 2] + "." + this.authority;
                    return auth.equals(authority);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    public static MyReactiveAuthorizationManager hasAuthority(String authority) {
        Assert.notNull(authority, "authority cannot be null");
        return new MyReactiveAuthorizationManager(authority);
    }
}
