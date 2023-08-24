package tw.com.jinglingshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.service.ProductService;
import tw.com.jinglingshop.service.ShoppingCartService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:ShoppingCartController
 * Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/17 下午 11:51
 * @Version 1.0
 */
@RestController
public class ShoppingCartController {


    @Autowired
    ShoppingCartService shoppingCartService;

    //將商品加入購物車
    @PostMapping("/productAddShoppingCart")
    public Result productAddShoppingCart(@RequestBody String body,@CookieValue(name ="jwt")String cookieValue){
        String email= JwtUtil.getUserEmailFromToken(cookieValue);
        JSONObject object = new JSONObject(body);
        Integer secondId =object.get("secondId")!=null?(Integer) object.get("secondId"):0;
        Integer productPageId =object.get("productPageId")!=null?(Integer) object.get("productPageId"):0;
        Integer count =object.get("count")!="null"?(Integer) object.get("count"):0;
        Integer mainId =object.get("mainId")!=null?(Integer) object.get("mainId"):0;
        String msg = shoppingCartService.addShoppingCart(email, productPageId, mainId, secondId, count);
        if ("加入成功".equals(msg)){
          return   Result.success(msg);
        }else {
           return Result.error(msg);
        }
    }


    //搜尋使用者購物車
    @PostMapping("/selectUserShoppingCart")
    public Result selectUserShoppingCart(@CookieValue(name ="jwt")String cookieValue){
        String email= JwtUtil.getUserEmailFromToken(cookieValue);
        List<Map<String, Object>> maps = shoppingCartService.selectUserShoppingCartByUserEmail(email);
        return Result.success(maps);
    }

    //更新購物車數量
    @PutMapping("/changeShoppingCartProductCount")
    public Result changeShoppingCartProductCount(@RequestBody String body){
        JSONObject object = new JSONObject(body);
        Integer id=object.get("id")!=null?(Integer) object.get("id"):0;
        Integer count=object.get("count")!=null?(Integer)object.get("count"):0;
        if(id>0&&count>0){
            String msg = shoppingCartService.updateShoppingCartByUserEmailAndShoppingCartId(id, count);
            if ("更新成功".equals(msg)){
                return Result.success("msg");
            }
            return Result.error(msg);
        }
        return Result.error("資訊錯誤");
    }

    //刪除購物車商品
    @DeleteMapping("/deleteShoppingCartProduct")
    public Result deleteShoppingCartProduct(@RequestBody String body){
        JSONObject object = new JSONObject(body);
        JSONArray idsJSONArray = object.getJSONArray("ids");
        if(idsJSONArray.length()>0){
            ArrayList<Integer> idList = new ArrayList<>();
            for (int i = 0; i < idsJSONArray.length(); i++) {
                idList.add((Integer) idsJSONArray.get(i));
            }
            shoppingCartService.deleteShoppingCartByIds(idList);
          return  Result.success("刪除成功");
        }else {
            return Result.error("未選擇刪除商品");
        }
    }

    //根據購物車選取的商品推薦最佳優惠券
    @PostMapping("/ShoppingCartBestCoupon")
    public Result ShoppingCartBestCoupon(@CookieValue(name ="jwt")String cookieValue,@RequestBody String body){
        JSONObject object = new JSONObject(body);
        JSONArray idsJSONArray = object.getJSONArray("cartIds");
        String email= JwtUtil.getUserEmailFromToken(cookieValue);
        if(idsJSONArray.length()>0){
            ArrayList<Integer> idList = new ArrayList<>();
            for (int i = 0; i < idsJSONArray.length(); i++) {
                idList.add((Integer) idsJSONArray.get(i));
            }
            ArrayList<HashMap<String, Object>> hashMaps = shoppingCartService.shoppingCartsBestCoupon(idList, email);
            return Result.success(hashMaps);
        }else {
           return Result.error();
        }
    }
}
