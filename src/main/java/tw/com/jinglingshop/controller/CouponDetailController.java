package tw.com.jinglingshop.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.service.CouponDetailService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:7777")
public class CouponDetailController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CouponDetailService couponDetailService;

	
	 @PostMapping("/addUserCoupon")
	    public Result addUserCoupon(@RequestBody Coupon coupon, @CookieValue(name = "jwt") String cookieValue) {
	        
	        String email = JwtUtil.getUserEmailFromToken(cookieValue);
	        User user = userService.getUserByEmail(email);

	        String couponCode = coupon.getCode();
	        
	        Coupon couponData = couponDetailService.findCouppnByCode(couponCode);
	        
	        
	        
	        if(couponData == null) {
	            return Result.error("查無此優惠券"); 
	        }

	        //拿到一個人可以領取幾張數量
	        Byte perPersonQuota = couponData.getPerPersonQuota();
	        //拿到Total可使用張數
	        Integer availableNumber= couponData.getAvailableNumber();
	        //拿到已領取數
	        Integer received= couponData.getReceived();
	        //使用數
	        Integer used = couponData.getUsed();
	        //CouponId
	        Integer couponId = couponData.getId();
	        //優惠券開始時間
	        LocalDateTime startTime = couponData.getStartTime();
	        //優惠券結束時間
	        LocalDateTime endTime = couponData.getEndTime();
	        
	        LocalDateTime currentTime = LocalDateTime.now();
	        if(startTime.isAfter(currentTime)) {
	        	return Result.error("優惠券尚未開始領取", startTime);
	        	
	        }
	        
	        if(endTime.isBefore(currentTime)) {
	        	return Result.error("優惠券已過期");
	        	
	        }
	        
	        //取得使用者id
	        Integer userId = user.getId();
	        //確認使用者是否已領取過優惠券
	        Boolean  alreadyHasACoupon= couponDetailService.checkUserAlreadyHasACoupon(userId, couponId);
	   
	        if(alreadyHasACoupon) {
	        	return Result.error("已領取過優惠券");
	        	
	        }
	        
	        
	        
	        Integer newAvailableNumber;
	        Integer newReceived;

	        if(perPersonQuota <= availableNumber) {
	            newAvailableNumber = availableNumber - perPersonQuota;
	            newReceived = received + perPersonQuota;
	        } else if(availableNumber > 0) {
	            newReceived = received + availableNumber;
	            newAvailableNumber = 0; // 所有的優惠券都被領走了
	        } else {
	            return Result.error("優惠券已全部領完");
	        }

	        if(newReceived.equals(used)) {
	            return Result.error("優換券已全數兌換完畢");
	        }

	        //更新coupon資料
	        couponDetailService.UpdateCoupon(newAvailableNumber, newReceived,couponId);
	        
	        //新增使用者coupon
	        CouponDetail couponDetail = new CouponDetail();
	        couponDetail.setUser(user);
	        couponDetail.setCoupon(couponData);
	        couponDetail.setCouponCount(perPersonQuota);
	        
	        Boolean result = couponDetailService.addUserCoupon(couponDetail);
	        
	        if(result) {
	            return Result.success("success");
	        }
	        return Result.error("fail");
	    }
		
	 
	 @PostMapping("/findAllUserCoupon")
	 public Result findAllUserCoupon(@CookieValue(name = "jwt") String cookieValue) {
		 
		 String email = JwtUtil.getUserEmailFromToken(cookieValue);
	        User user = userService.getUserByEmail(email);
	        Integer userId= user.getId();
	        
	        List<Coupon> couponList= couponDetailService.fillAllUserCoupon(userId);
	        
	        if(couponList.isEmpty()) {
	            return Result.error("尚未有優惠券");
	        }
		 
		 
		 return Result.success("success", couponList);
	 }
	

}
