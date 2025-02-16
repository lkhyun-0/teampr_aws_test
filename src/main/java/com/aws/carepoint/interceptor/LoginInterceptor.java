package com.aws.carepoint.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 세션에서 로그인 여부 확인
        Object userPk = request.getSession().getAttribute("userPk");

        if (userPk == null) {
            // 현재 요청 URL (쿼리스트링 포함)
            String targetUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }
            // 세션에 저장 (추후 로그인 성공 시 사용)
            request.getSession().setAttribute("redirectUrl", targetUrl);

            // 로그인 페이지로 리다이렉트
            response.sendRedirect("/user/signIn");
            return false;
        }
        return true;
    }
}
