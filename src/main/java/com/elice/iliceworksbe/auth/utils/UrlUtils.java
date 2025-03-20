package com.elice.iliceworksbe.auth.utils;


public class UrlUtils {
    public static final String[] PermittedUrl = {
            "/api/auth/verify-email",
            "/api/auth/verify",
            "/api/auth/verify-email-password",
            "/api/auth/change-password/by-email",
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/validate-email",
            "/swagger-ui/**",
            "/api-docs/**",
            "/api/accept/**",
            "/favicon.ico",
            "/actuator/**",
            "/grafana/**"
    };
}
