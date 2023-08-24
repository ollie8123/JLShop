package tw.com.jinglingshop.service;

import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.jinglingshop.model.dao.CouponDetailRepository;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;

import java.util.List;

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

    public List<CouponDetail> selectCurrentlyAvailableCouponDetailByUserIdAndSellerId(Integer userId, List<Integer> couponIds){
        return  couponDetailRepository.selectCurrentlyAvailableByUserIdAndSellerId(userId, couponIds);
    }

}
