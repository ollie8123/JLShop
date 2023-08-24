package tw.com.jinglingshop.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import tw.com.jinglingshop.service.CouponService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

import java.util.Map;

/**
 * ClassName:CouponController
 * Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/16 下午 07:52
 * @Version 1.0
 */
@RestController
public class CouponController {
    @Autowired
    CouponService couponService;

    @PostMapping("/addCoupon")
    public Result addCoupon(@RequestParam Integer couponId, @CookieValue(name ="jwt")String cookieValue){
        String email= JwtUtil.getUserEmailFromToken(cookieValue);
        String msg = couponService.addCoupon(couponId, email);
        return  Result.success(msg);
    }

}
