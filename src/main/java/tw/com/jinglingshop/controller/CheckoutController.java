package tw.com.jinglingshop.controller;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.service.CheckoutService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

/**
 * ClassName:CheckoutController
 * Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/27 上午 12:40
 * @Version 1.0
 */
@RestController
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;


    
    //根據選取的購物車品，返回結帳頁面資訊
    //@CookieValue(name ="jwt")String cookieValue
    @PostMapping("/checkOutGet")
    public Result checkOutGet(@RequestBody Map<String,Object> body,@CookieValue(name ="jwt")String cookieValue){
        if(body.get("cartIds")!=null){
            //購物車選取的商品列表
            ArrayList<Integer> cartIds =(ArrayList<Integer>)body.get("cartIds") ;
            if(!cartIds.isEmpty()){
                String email = JwtUtil.getUserEmailFromToken(cookieValue);
                Map<String, Object> stringObjectMap = checkoutService.checkoutDetails(cartIds, email);
                return Result.success(stringObjectMap);
            }
            return Result.error("cartIds為空");
        }
        return Result.error("沒有cartIds");
    }
    //@CookieValue(name ="jwt")String cookieValue
    @PostMapping("/generateOrders")
    public Result generateOrders(@RequestBody Map<String,Object> body,@CookieValue(name ="jwt")String cookieValue){
        JSONObject object = new JSONObject(body);
        String email = JwtUtil.getUserEmailFromToken(cookieValue);

        String s = null;
        try {
            s = checkoutService.generateOrders(object, email);
        } catch (Exception e) {
            System.out.println("錯誤");
            return Result.error();
        }

        if("新增成功".equals(s)){
            return Result.success();
        }else {
            System.out.println(s);
            return Result.error("s");
        }

    }


}
