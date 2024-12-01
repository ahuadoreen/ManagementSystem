package com.example.ms.component;

//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;


//@Component("auth")
public class MyAuthorization {
//    public boolean hasAuthority() {
//        return hasAuthority(null);
//    }

//    public boolean hasAuthority(String authority) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
//        if (authorities.contains(Constant.ROLE_SUPER_ADMIN)) {
//            return true;
//        }
//        HttpServletRequest request = WebUtils.getRequest();
//        String uri = request.getRequestURI();
//        String[] uris = uri.split("/");
//        if (authority == null) {
//            authority = uris[uris.length - 1];
//        }
//        authority = uris[uris.length - 2] + "." + authority;
//        return authorities.contains(authority);
//    }
}
