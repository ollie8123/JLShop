package tw.com.jinglingshop.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.coupon.Coupon;

import java.util.List;

/**
 * ClassName:CouponRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/15 下午 10:08
 * @Version 1.0
 */
public interface CouponRepository  extends JpaRepository<Coupon,Integer> {

    //根據頁面id搜尋賣家的優惠券
    @Query("select p.seller.coupons from ProductPage p where p.id=:pageId")
    List<Coupon>findCouponsByProductPageId(@Param("pageId")Integer pageId);

    //根據賣家id搜尋賣家的優惠券
    @Query("select p.seller.coupons from ProductPage p where p.seller.id=:sellerId")
    List<Coupon>findCouponsBySellerId(@Param("sellerId")Integer sellerId);

   //搜尋優惠券，根據賣家ID、低消小於price、開始時間小於當前時間、結束時間大於當前時間、可使用數大於0
   @Query(value = "select * from coupon  where minium_spending_amount<=:price AND end_time> GETDATE() AND start_time< GETDATE() AND available_number>0 AND seller_id=:sellerId ",nativeQuery = true)
   List<Coupon>BestCoupons(@Param("price")Integer price,@Param("sellerId")Integer sellerId);

}
