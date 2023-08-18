package tw.com.jinglingshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ClassName:SecurityConfig
 * Package:tw.com.myispan.config
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/22 上午 04:08
 * @Version 1.0
 */
@Configuration
public class SecurityConfig {

    
    @Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
