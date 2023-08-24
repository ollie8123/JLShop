package tw.com.jinglingshop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.jinglingshop.model.dao.CouponDetailRepository;
import tw.com.jinglingshop.model.dao.CouponRepository;
import tw.com.jinglingshop.model.dao.ProductPageRepository;
import tw.com.jinglingshop.model.dao.UserRepository;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.user.User;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * ClassName:CouponService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/15 下午 10:12
 * @Version 1.0
 */
@Service
public class CouponService {
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    ProductPageRepository productPageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CouponDetailRepository couponDetailRepository;

    //根據商品頁面回傳賣家優惠券及使用者持有的此賣家優惠券
    public HashMap<String,Object> findSellerCouponByPageId(Integer pageId,String userEmail){
        HashMap<String,Object>userCouponAndSellerCoupon=new HashMap<>();
        List<HashMap<String,Object>> couponMsg=new ArrayList<>();
        List<Coupon> Coupons = couponRepository.findCouponsByProductPageId(pageId);
        for (Coupon c:Coupons) {
            //判斷優惠券在結束時間前
            if(c.getEndTime().isAfter(LocalDateTime.now())){
                HashMap<String, Object> couponMap = new HashMap<>();
                couponMap.put("id",c.getId());
                couponMap.put("startTime",c.getStartTime());
                couponMap.put("endTime",c.getEndTime());
                couponMap.put("minSpending",c.getMiniumSpendingAmount());
                couponMap.put("perPersonQuota",c.getPerPersonQuota());
                couponMap.put("perPersonQuota",c.getPerPersonQuota());
                if(c.getDiscountRate()!=null){
                    couponMap.put("type","rate");
                    couponMap.put("Discount",c.getDiscountRate());
                }else {
                    couponMap.put("type","amount");
                    couponMap.put("Discount",c.getDiscountAmount());
                }
                couponMsg.add(couponMap);
            }
        }
        userCouponAndSellerCoupon.put("seller",couponMsg);
        ArrayList<Integer> userList = new ArrayList<>();
        Optional<ProductPage> page = productPageRepository.findById(pageId);
        if (page.isPresent()){
           int sellerId=page.get().getSeller().getId();
            Optional<User> user = userRepository.findByEmail(userEmail);
            if(user.isPresent()){
                userCouponAndSellerCoupon.put("userType","login");
                List<CouponDetail> couponDetails = user.get().getCouponDetails();
                for (CouponDetail c:couponDetails) {
                   if((c.getCoupon().getSeller().getId()==sellerId)&&(c.getCoupon().getEndTime().isAfter(LocalDateTime.now()))){
                       userList.add(c.getCoupon().getId());
                   }
                }
            }else {
                userCouponAndSellerCoupon.put("userType","notLogin");
            }
        }
        userCouponAndSellerCoupon.put("userCoupon",userList);
        return userCouponAndSellerCoupon;
    }

    //新增優惠券
    public String addCoupon(Integer couponId,String userEmail){
        Optional<User> User = userRepository.findByEmail(userEmail);
        Optional<Coupon> Coupon = couponRepository.findById(couponId);
        if(Coupon.isPresent()){
            System.out.println("獲取111"+Coupon);
            if(Coupon.get().getEndTime().isAfter(LocalDateTime.now())){
                if (User.isPresent()){
                    Optional<CouponDetail> couponDetailByUserIdAndCouponId = couponDetailRepository.findCouponDetailByUserIdAndCouponId(User.get().getId(), couponId);
                    if(couponDetailByUserIdAndCouponId.isPresent()){
                            return "已達領";
                    }else {
                        CouponDetail couponDetail = new CouponDetail();
                        couponDetail.setUser(User.get());
                        couponDetail.setCoupon(Coupon.get());
                        couponDetail.setCouponCount(Coupon.get().getPerPersonQuota());
                        couponDetailRepository.save(couponDetail);
                        return "領取成功";
                    }
                }
                return "優惠券已過期";
            }
        }
        return "無此優惠券";
    }

    //根據金額及賣家id搜尋符合的優惠券
    public  List<Coupon> selectEfficientCouponByPriceAndSellerId(Integer price,Integer sellerId){
        List<Coupon> coupons = couponRepository.BestCoupons(price, sellerId);
        return coupons;
    }

}
