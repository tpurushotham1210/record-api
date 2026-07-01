package com.teletracnavman.security;

import org.springframework.http.HttpMethod;

import jakarta.servlet.http.HttpServletRequest;

public final class ApiRequestRules {

    private ApiRequestRules() {
    }

    public static boolean isH2Console(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/h2-console");
    }

    public static boolean isAllowedPostEndpoint(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }
        String uri = request.getRequestURI();
        return "/echo".equals(uri)
                || "/device".equals(uri)
                || uri.endsWith("/nocontent");
    }
}
