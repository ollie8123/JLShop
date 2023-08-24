package tw.com.jinglingshop.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

/**
 * ClassName:LoginConfig
 * Package:tw.com.myispan.config
 * Description:
 * 
 * @Author chiu
 * @Create 2023/7/22 上午 05:02
 * @Version 1.0
 */
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/userLogin")
    public ResponseEntity<String> userLogin(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登入失敗");
        }
        Result checkLogin = userService.checkLogin(email, password);
        if (checkLogin.getCode() == 1) {
            String token = JwtUtil.GenerateToken(email);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.ok("登入成功");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登入失敗");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);   // 將cookie設置過期
        response.addCookie(cookie);
        return ResponseEntity.ok("logout success");
    }


    // 前端轉跳頁面時，驗證token有效性
    @PostMapping("/login/tokenChecking")
    public Result tokenChecking(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("jwt")) {
                    if (JwtUtil.isJwtTokenValid(c.getValue())) {
                        return Result.success("驗證成功");
                    } else {
                        return Result.error("Jwt驗證失敗");
                    }
                }
            }
            return Result.error("查無JwtCookie");
        } else {
            return Result.error("沒有cookies");
        }
    }

    // 登入按鈕獲取google資料，以email來產生token
    // 還未做google登入後連接資料庫的操做
    @PostMapping("/login/googleLogin")
    public ResponseEntity<String> google(@RequestBody Map<String, Object> google, HttpServletResponse response){
        if (google.containsKey("jwt")) {
            System.out.println(google.get("jwt"));
        }
        Map<String, String> googles = (Map<String, String>) google.get("googleMsg");
        String email = googles.get("email");
        System.out.println(email);
        String token = JwtUtil.GenerateToken(email);

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok("1");
    }

}
