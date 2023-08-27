package tw.com.jinglingshop.config;

import java.io.IOException;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tw.com.jinglingshop.utils.JwtUtil;

/**
 * ClassName:AuthorizationInterceptor
 * Package:tw.com.jinglingshop.config
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/27 下午 03:34
 * @Version 1.0
 */
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        System.out.println("==================================");
        response.setCharacterEncoding("UTF-8");
        System.out.println("進入攔截器");
        // 檢查是否為預檢請求
        if (request.getMethod().equals("OPTIONS")) {
            System.out.println("放行OPTIONS");
            return true;
        }
        // 獲取url路徑
        String uri = request.getRequestURI();
        System.out.println("發送請求的路徑為:" + uri);
        if (
         uri.startsWith("/login") ||
         uri.startsWith("/public")||
         uri.startsWith("/userLogin")||
         uri.startsWith("/findUserByEmail")||
//         uri.startsWith("/userPhoto")||
//        uri.startsWith("/addUser")  //放行路徑的URL開頭
        uri.startsWith("/") // 驗證器放行所有路徑，測試用，網站正式上線時要調整
        ) {
            System.out.println("路徑為放行路徑");
            return true;
        } else {
            System.out.println("路徑為需驗證路徑");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("jwt")) {
                    if (JwtUtil.isJwtTokenValid(c.getValue())) {
                        return true;
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Jwt驗證失敗");
                        System.out.println("Jwt驗證失敗");
                        return false;
                    }
                }
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("查無JwtCookie");
            System.out.println("查無JwtCookie");
            return false;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("沒有cookies");
            System.out.println("沒有cookies");
            return false;
        }

    }

}
