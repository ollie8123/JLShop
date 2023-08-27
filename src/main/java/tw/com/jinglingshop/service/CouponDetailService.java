package tw.com.jinglingshop.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.CouponDetailRepository;
import tw.com.jinglingshop.model.dao.CouponRepository;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;

/**
 * ClassName:CouponDetailService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/24 上午 11:33
 * @Version 1.0
 */
@Service
public class CouponDetailService {
    @Autowired
    private CouponDetailRepository couponDetailRepository;
    
    @Autowired
    private CouponRepository couponRepository;

    public List<CouponDetail> selectCurrentlyAvailableCouponDetailByUserIdAndSellerId(Integer userId, List<Integer> couponIds){
        return  couponDetailRepository.selectCurrentlyAvailableByUserIdAndSellerId(userId, couponIds);
    }
    
    public Boolean addUserCoupon(CouponDetail couponDetail ) {
    	
    	if(couponDetail != null) {
    		couponDetailRepository.save(couponDetail);
        	
        	return true;
    	}else {
    		return false;
    	}
    }
    
    public Coupon findCouppnByCode(String code) {
    	
    		try {
				Optional<Coupon> couponList = couponRepository.findByCode(code);
				
				 if(couponList.isPresent()) {
			            return couponList.get();
			        }
				
			} catch (Exception e) {
				e.printStackTrace();		
			}	
    		return null;
    }
    
    
    public String UpdateCoupon(Integer newAvailableNumber,Integer newReceived,Integer couponId ) {
    	
    	 if (newAvailableNumber != null && newReceived != null && couponId != null) {
    	
    	Optional<Coupon> couponList = couponRepository.findById(couponId);
    	Coupon coupon  = couponList.get();
    	
    	
    	coupon.setAvailableNumber(newAvailableNumber);
    	coupon.setReceived(newReceived);
    	
    	couponRepository.save(coupon);

    	return "success";
    	 }else{
    		 return "fail";
    	 }
    	
    }
 
    public Boolean checkUserAlreadyHasACoupon(Integer userId ,Integer couponId ) {
    	
    	Optional<CouponDetail> userCoupon = couponDetailRepository.findByUserIdAndCouponId(userId,couponId);
    	
    	return userCoupon.isPresent();
    	
    }
    
    public List<Coupon> fillAllUserCoupon(Integer userId) {
    	
    	List<CouponDetail> couponDetailList= couponDetailRepository.findAllByUserId(userId);
    	
    	List<Integer> couponIds = new ArrayList<>();
    	for (CouponDetail detail : couponDetailList) {
    	    couponIds.add(detail.getCoupon().getId());
    	}
    	
    	if (couponIds.isEmpty()) {
            return Collections.emptyList();  // 返回一个空列表
        }    	 

    	return couponRepository.findAllById(couponIds);
    }
    

}
