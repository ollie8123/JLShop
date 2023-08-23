package tw.com.jinglingshop.utils;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.security.Key;
import java.util.Date;

/**
 * ClassName:JwtUtil
 * Package:tw.com.myispan.utils
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/23 下午 08:43
 * @Version 1.0
 */
public class JwtUtil {
    // 設定算法
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 設定JwtToken的有效時間(毫秒)
    // 7 days
    private static final long expirationTime = 604800000L;

    /**
     * 用傳入的email產生JwtToken
     * 
     * @param email google的email
     * @return 返回一個 JwtToken
     */
    public static String GenerateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }
    
    
    
    //產生Token效期
    public static Date getTokenExpiration(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        } catch (Exception e) {
            return null; 
        }
    }

    /**
     * 傳入JwtToken返回解析結果訊息
     * 
     * @param token JwtToken
     * @return 有效回傳true 無效false
     */
    public static Boolean isJwtTokenValid(String token) {
        String AA = null;
        try {
            AA = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            if (!AA.isEmpty() && AA != null) {
                return true;
            } else {
                return false;
            }
        } catch (ClaimJwtException e) {
            return false;
        } catch (SignatureException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 傳入JwtToken返回解析出來的email
     * 
     * @param token JwtToken
     * @return 回傳解析出來的email
     */
//    public static String getUseremail(String token) {
//        String AA = null;
//        try {
//            AA = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getSubject();
//            if (!AA.isEmpty() && AA != null) {
//                return "驗證成功";
//            } else {
//                return "驗證失敗";
//            }
//        } catch (ClaimJwtException e) {
//            return "驗證過期";
//        } catch (SignatureException e) {
//            return "驗證失敗";
//        }
//    }

    public static String getUserEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ClaimJwtException e) {
        	 return "驗證過期";
        } catch (SignatureException e) {
        	return "驗證失敗";
        } catch (Exception e) {
            return null;
        }
    }
}

