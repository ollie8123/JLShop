package tw.com.jinglingshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName:CorsConfig
 * Package:com.example.zdemo01springboot.config
 * Description:
 *
 * @Author chiu
 * @Create 2023/6/28 下午 08:34
 * @Version 1.0
 */
@Configuration
// @EnableWebMvc 啟用這個會讓Spring Boot 產生的 Web Mvc 設定全都清除，會把Json轉換器的設定也清除
public class CorsConfig implements WebMvcConfigurer {
    // 實作以上介面便可進行WebMvc的設定

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 決定CORS設定的適用範圍，/** 意味著適用全部路徑
                .allowedOrigins("http://localhost:7777")
                // 設定請求來源，僅允許來自以上網址的請求會受理
                // .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允許的Http Method
                // .allowedHeaders("Content-Type")
                // 允許的請求頭，以上只允許了Content-Type這個請求類型
                .allowCredentials(true);
        // 啟用驗證功能，以允許檢驗跨來源請求中的驗證資訊
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor());
        // 將AuthorizationInterceptor這個自訂攔截器加入攔截器清單的方法
    }
}
