/*package com.aws.carepoint.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 세션에서 로그인 여부 확인
        Object userPk = request.getSession().getAttribute("userPk");

        if (userPk == null) {

            // JavaScript alert 추가
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("""
                <script>
                    alert('로그인이 필요합니다.');
                    location.href='/user/signIn';
                </script>
            """);
            return false;
        }
        return true; // 로그인했으면 요청 계속 진행
    }
}*/
