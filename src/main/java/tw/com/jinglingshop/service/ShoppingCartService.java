package tw.com.jinglingshop.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tw.com.jinglingshop.model.dao.ProductPagePhotoRepository;
import tw.com.jinglingshop.model.dao.SellerRepository;
import tw.com.jinglingshop.model.dao.ShoppingCartRepository;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;
import tw.com.jinglingshop.model.domain.order.ShoppingCart;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.utils.Result;
import tw.com.jinglingshop.utils.photoUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName:ShoppingCartService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/18 上午 01:36
 * @Version 1.0
 */
@Transactional
@Service
public class ShoppingCartService {
    @Autowired
   private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ProductPagePhotoRepository productPagePhotoRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private CouponService couponService;
    @Autowired
    private  UserService userService;
    @Autowired
    private  ProductService productService;
    @Autowired
    private CouponDetailService couponDetailService;


    //檢查選擇的商品庫存

    public boolean checkAvailability(@RequestBody String body) {
        JSONArray objects = new JSONArray(body);
        if(objects.length()>0){
            for (int i=0;i<objects.length();i++){
                int id = objects.getJSONObject(i).getInt("id");
                int count =  objects.getJSONObject(i).getInt("count");
                Optional<ShoppingCart> ShoppingCart = shoppingCartRepository.findById(id);
                if(ShoppingCart.isPresent()){
                    //如果庫存大於等於選擇數量
                    if( ShoppingCart.get().getProduct().getStocks()<count){
                        return false;
                    }
                }else {
                    return false;
                }
            }
            return true;
        }else {
            return false;
        }
    }
    public boolean  deleteShoppingCart(ShoppingCart shoppingCart){
        try {
            shoppingCartRepository.delete(shoppingCart);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public ShoppingCart  selectShoppingCartById(Integer CartId){
        Optional<ShoppingCart> ShoppingCart = shoppingCartRepository.findById(CartId);
        if(ShoppingCart.isPresent()){
            return ShoppingCart.get();
        }else {
            return null;
        }

    }

    public List<ShoppingCart>  selectShoppingCartByCartIds(ArrayList<Integer> CartIds){
      return shoppingCartRepository.selectShoppingCartByCartIds(CartIds);
    }


    //新增商品進購物車
    public  String addShoppingCart(String email,Integer productPageId,Integer mainId,Integer secondId,Integer count) {
        if (count<1){
            return "數量錯誤";
        }
        Product product = productService.selectProductBy(productPageId, mainId, secondId);
        if(product!=null){
            User user = userService.getUserByEmail(email);
            if (user!=null){
                if(product.getProductPage().getSeller().getUser().getId()!=user.getId()){
                    Optional<ShoppingCart> optionalShoppingCart = shoppingCartRepository.findAllByUserIdAndProductId(user.getId(), product.getId());
                    if(optionalShoppingCart.isPresent()){
                        ShoppingCart shoppingCart = optionalShoppingCart.get();
                        if(product.getStocks()>=(shoppingCart.getProductQuantity()+count)){
                            shoppingCart.setProductQuantity((short) (shoppingCart.getProductQuantity()+count));
                            shoppingCartRepository.save(shoppingCart);
                            return "加入成功";
                        }else {
                            return "加入購物車數量大於庫存";
                        }
                    }else {
                        if (product.getStocks()>=count){
                            ShoppingCart shoppingCart = new ShoppingCart();
                            shoppingCart.setSeller(product.getProductPage().getSeller());
                            shoppingCart.setProduct(product);
                            shoppingCart.setProductQuantity(count.shortValue());
                            shoppingCart.setUser(user);
                            shoppingCartRepository.save(shoppingCart);
                            return "加入成功";
                        }
                        return "加入購物車數量大於庫存";
                    }
                }else {
                    return "不可新增自己的商品";
                }
            }else {
                return  "使用者資訊錯誤";
            }
        }else {
            return "商品訊息錯誤";
        }
    }
    //搜尋使用者購物車資訊
    public List<Map<String,Object>> selectUserShoppingCartByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        Page<Map<String, Object>> allSellerIdByUserId = shoppingCartRepository.findAllSellerIdByUserId(user.getId(), PageRequest.of(0,5));
        List<Map<String,Object>> productList =new ArrayList<>();
        for (Map<String, Object> i:allSellerIdByUserId) {
            HashMap<String, Object> resMap = new HashMap<>();
            resMap.put("id",i.get("id"));
            resMap.put("account",i.get("account"));
            resMap.put("sellerName",i.get("sellerName"));
            List<ShoppingCart> selectList = shoppingCartRepository.findAllByUserIdAndSellerId(user.getId(), (Integer) i.get("id"));
            List<HashMap<String,Object>> listMsg=new ArrayList<>();
            for (ShoppingCart s:selectList) {
                HashMap<String, Object> productMsg = new HashMap<>();
                productMsg.put("cartId", s.getId());
                productMsg.put("sellerId", s.getSeller().getId());
                productMsg.put("productName", s.getProduct().getProductPage().getName());
                productMsg.put("productMainId", s.getProduct().getMainSpecificationClassOption().getId());
                productMsg.put("productMainName", s.getProduct().getMainSpecificationClassOption().getName());
                productMsg.put("productSecondId", s.getProduct().getSecondSpecificationClassOption().getId());
                productMsg.put("productSecondName", s.getProduct().getSecondSpecificationClassOption().getName());
                productMsg.put("productPrice",s.getProduct().getPrice());
                productMsg.put("productPage",s.getProduct().getProductPage().getId());
                productMsg.put("productCount",s.getProductQuantity());
                if(s.getProduct().getStocks()>0){
                    productMsg.put("stocks",s.getProduct().getStocks());
                    productMsg.put("stocksType","inStock");
                }else {
                    productMsg.put("stocks",s.getProductQuantity());
                    productMsg.put("stocksType","emptyStocks");
                }
                if("~NoSecondSpecificationClass".equals(s.getProduct().getMainSpecificationClassOption().getClassName())){
                    ProductPagePhoto productPagePhoto = productPagePhotoRepository.selectProductPagePhotoProductSerialNumberByPageId(s.getProduct().getProductPage().getId());
                   // productMsg.put("productPhoto",productPagePhoto.getPhotoPath());
                    productMsg.put("productPhoto",photoUtil.getBase64ByPath(productPagePhoto.getPhotoPath()));

                }else {
                   // productMsg.put("productPhoto",s.getProduct().getMainSpecificationClassOption().getPhotoPath());
                    productMsg.put("productPhoto",photoUtil.getBase64ByPath(s.getProduct().getMainSpecificationClassOption().getPhotoPath()));
                }
                listMsg.add(productMsg);
            }

            //搜尋賣家優惠券跟使用者擁有的此買家優惠券
            Optional<Seller> seller = sellerRepository.findById((Integer) i.get("id"));
            if(seller.isPresent()){
                HashMap<String, Object> sellerAndUser = couponService.findSellerCouponByPageId( seller.get().getProductPages().get(0).getId(), email);
                resMap.put("sellerAndUser",sellerAndUser);
            }
            resMap.put("productMsg",listMsg);
            productList.add(resMap);
        }
        return productList;
    }

    //購物車更新
    public String updateShoppingCartByUserEmailAndShoppingCartId(Integer shoppingCartId,Integer count){
        Optional<ShoppingCart> OptionalshoppingCart = shoppingCartRepository.findById(shoppingCartId);
        if(OptionalshoppingCart.isPresent()){
            ShoppingCart shoppingCart = OptionalshoppingCart.get();
            //如果更新數量<庫存數量
            if(count<=shoppingCart.getProduct().getStocks()){
                shoppingCart.setProductQuantity(count.shortValue());
                shoppingCartRepository.save(shoppingCart);
                return "更新成功";
            }else {
                shoppingCart.setProductQuantity(shoppingCart.getProduct().getStocks());
                shoppingCartRepository.save(shoppingCart);
                return "此商品最大購買量"+shoppingCart.getProduct().getStocks();
            }
        }else {
            return "無此商品";
        }
    }

    //根據id陣列刪除購物車商品
    public void deleteShoppingCartByIds(ArrayList<Integer> idList) {
            shoppingCartRepository.deleteShoppingCartsByIds(idList);
    }

    //根據選取商品，搜尋當前可用的優惠券、與使用者持有的優惠券並計算優惠券
    public  ArrayList<HashMap<String,Object>>  shoppingCartsBestCoupon(ArrayList<Integer> idList, String email) {
        ArrayList<HashMap<String,Object>> resList = new ArrayList<>();
        User user = userService.getUserByEmail(email);
        //獲取群組化之後的sellerId列表
        List<Integer> sellerIds = shoppingCartRepository.selectShoppingCartSellerIdByCartIdsGroupSellerId(idList);
        for (Integer sellerId:sellerIds) {
            HashMap<String, Object> sellerMap = new HashMap<>();
            sellerMap.put("sellerId",sellerId);
            //sellerCoupon訊息陣列
            List<HashMap<String,Object>>sellerCoupon=new ArrayList<>();
            //搜尋賣家現在可用的優惠券
            List<Coupon> nowCoupon = couponService.selectEfficientCouponByPriceAndSellerId(999999, sellerId);
            //儲存id用陣列
            ArrayList<Integer> couponIds = new ArrayList<>();

            for (Coupon c:nowCoupon) {
                //單筆sellerCoupon訊息陣列
                HashMap<String, Object> sellerCouponMsg = new HashMap<>();
                sellerCouponMsg.put("couponId",c.getId());
                sellerCouponMsg.put("startTime",c.getStartTime());
                sellerCouponMsg.put("endTime",c.getEndTime());
                sellerCouponMsg.put("miniumSpendingAmoun",c.getMiniumSpendingAmount());
                if(c.getDiscountRate()!=null){
                    sellerCouponMsg.put("type","rate");
                    sellerCouponMsg.put("discountRate",c.getDiscountRate());
                    sellerCouponMsg.put("discountMaximum",c.getDiscountMaximum());
                }else {
                    sellerCouponMsg.put("type","amount");
                    sellerCouponMsg.put("discountAmount",c.getDiscountAmount());
                }
                sellerCoupon.add(sellerCouponMsg);
                couponIds.add(c.getId());
            }
            sellerMap.put("sellerCoupon",sellerCoupon);
            //根據賣家現在可用的優惠券+userId搜尋使用者持有那些
            List<CouponDetail> couponDetails = couponDetailService.selectCurrentlyAvailableCouponDetailByUserIdAndSellerId(user.getId(), couponIds);
            //sellerCoupon訊息陣列
            List<HashMap<String,Object>>userCoupon=new ArrayList<>();
            for (CouponDetail c:couponDetails) {
                //單筆sellerCoupon訊息陣列
                HashMap<String, Object> userCouponMsg = new HashMap<>();
                userCouponMsg.put("couponId",c.getCoupon().getId());
                userCouponMsg.put("couponCount",c.getCouponCount());
                userCoupon.add(userCouponMsg);
            }
            sellerMap.put("userCoupon",userCoupon);
            //計算賣家為i的總選取金額
            int price=0;

            //獲取跟idList裡面id的購物車
            List<ShoppingCart> shoppingCarts = shoppingCartRepository.selectShoppingCartByCartIds(idList);
            //獲取shoppingCarts裡面id為i的 List<ShoppingCart>
            List<ShoppingCart> collect = shoppingCarts.stream().filter(cart -> cart.getSeller().getId() == sellerId).collect(Collectors.toList());
            for (ShoppingCart s:collect) {
                //此賣家計算消費總金額
                price+=(s.getProductQuantity()*s.getProduct().getPrice());
            }
            //搜尋優惠券，根據賣家ID、低消小於price、開始時間小於當前時間、結束時間大於當前時間、可使用數大於0
            List<Coupon> coupons = couponService.selectEfficientCouponByPriceAndSellerId(price, sellerId);
            if(coupons.size()>0){
                //最大扣除金額、最優優惠券
                int Max=0;
                Coupon beastCoupon=coupons.get(0);
                for (Coupon c: coupons) {
                    //領取狀態
                    String type=null;
                    //查詢使用者是否有領取過此優惠券
                    List<CouponDetail> userCouponDetail = user.getCouponDetails().stream().filter(couponDetail -> couponDetail.getCoupon().getId() == c.getId()).collect(Collectors.toList());
                    //有領掛過
                    if(userCouponDetail.size()>0){
                        //查詢是否還有可用數量
                        //有可用數量
                        if(userCouponDetail.get(0).getCouponCount()>0){
                            type="有可用數量";
                            //無可用數量
                        }else {
                            continue;
                        }
                        //沒領過
                    }else {
                        type="沒領過";
                    }
                    //檢查此優惠券是否為折扣
                    if(c.getDiscountRate()!=null){
                        //計算折扣後，最大扣除金額是否比限制最大金額高
                        if(price-(price*c.getDiscountRate())>c.getDiscountMaximum()){
                            //比最大金額大，只取最大金額
                            //判斷折扣金額是否超過當前折最多的，是就把此優惠券設為最優
                            if(Max<c.getDiscountMaximum()){
                                Max=c.getDiscountMaximum();
                                beastCoupon=c;
                                //如果相等
                            }else if(Max==c.getDiscountMaximum()){
                                //比較誰的低消高(如果最好的優惠券低消小於當前優惠券就取代最好優惠券)
                                if(beastCoupon.getMiniumSpendingAmount()<c.getMiniumSpendingAmount()){
                                    Max=c.getDiscountMaximum();
                                    beastCoupon=c;
                                    //如果低消相同比對結束時間(如果beastCoupon.getEndTime()結束時間是在c.getEndTime()之後)
                                }else if(beastCoupon.getEndTime().isAfter(c.getEndTime())){
                                    Max=c.getDiscountMaximum();
                                    beastCoupon=c;
                                }
                            }
                            //未超過限制金額
                        }else {
                            //如果未超過限制大於當前折扣
                            if(Max<(price-(price*c.getDiscountRate()))){
                                Max=(int)(price-(price*c.getDiscountRate()));
                                beastCoupon=c;
                                //如果相等比較
                            }else if(Max==price-(price*c.getDiscountRate())){
                                //比較誰的低消高(如果最好的優惠券低消小於當前優惠券就取代最好優惠券)
                                if(beastCoupon.getMiniumSpendingAmount()<c.getMiniumSpendingAmount()){
                                    Max=(int)(price-(price*c.getDiscountRate()));
                                    beastCoupon=c;
                                    //如果低消相同比對結束時間(如果beastCoupon.getEndTime()結束時間是在c.getEndTime()之後)
                                }else if(beastCoupon.getEndTime().isAfter(c.getEndTime())){
                                    Max=(int)(price-(price*c.getDiscountRate()));
                                    beastCoupon=c;
                                }
                            }
                        }
                        //固定折扣
                    }else {
                        //如果固定折扣大於當前折扣
                        if(Max<c.getDiscountAmount()){
                            Max=c.getDiscountAmount();
                            beastCoupon=c;
                            //如果相等比較
                        }else if(Max==c.getDiscountAmount()){
                            //比較誰的低消高(如果最好的優惠券低消小於當前優惠券就取代最好優惠券)
                            if(beastCoupon.getMiniumSpendingAmount()<c.getMiniumSpendingAmount()){
                                Max=c.getDiscountAmount();
                                beastCoupon=c;
                                //如果低消相同比對結束時間(如果beastCoupon.getEndTime()結束時間是在c.getEndTime()之後)
                            }else if(beastCoupon.getEndTime().isAfter(c.getEndTime())){
                                Max=c.getDiscountAmount();
                                beastCoupon=c;
                            }
                        }
                    }
                }
                sellerMap.put("bestCouponId",beastCoupon.getId());
            }else {
            }
            resList.add(sellerMap);
        }
        return resList;
    }
}
