package tw.com.jinglingshop.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;

import java.util.List;
import java.util.Optional;

/**
 * ClassName:CouponDetailRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/16 下午 07:38
 * @Version 1.0
 */
public interface CouponDetailRepository extends JpaRepository<CouponDetail,Integer> {

    Optional<CouponDetail>findCouponDetailByUserIdAndCouponId(@Param("userId")Integer userId,@Param("couponId")Integer couponId);


    //根據couponIds
    @Query("select cd from  CouponDetail cd where cd.user.id=:userId AND cd.coupon.id in:couponIds")
    List<CouponDetail>selectCurrentlyAvailableByUserIdAndSellerId(@Param("userId")Integer userId,@Param("couponIds")List<Integer> couponIds);

    Optional<CouponDetail> findByUserIdAndCouponId(Integer userId, Integer couponId);
    
    List<CouponDetail> findAllByUserId(Integer userId);




}
