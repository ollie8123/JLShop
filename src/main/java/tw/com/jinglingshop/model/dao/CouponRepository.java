package tw.com.jinglingshop.model.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.user.Seller;

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
    
    //根據email找到sellerid
    @Query("SELECT s.id FROM Seller s WHERE s.user.email= :email ")
	 Integer findSellerIdByEmail( String email);
    
    
    @Query( "SELECT c FROM Coupon c WHERE endTime > :curretTime and availableNumber =0 and c.seller.Id= :sellerId ORDER BY dataUpdateTime DESC" )
    Page<Coupon> findNoVailbleNumber(Pageable pageable,LocalDateTime curretTime,Integer sellerId);
    
    @Query( "SELECT c FROM Coupon c WHERE endTime < :curretTime and c.seller.Id= :sellerId ORDER BY dataUpdateTime DESC" )
    Page<Coupon> findExpired(Pageable pageable,LocalDateTime curretTime,Integer sellerId);
    
    @Query( "SELECT c FROM Coupon c WHERE endTime > :curretTime and availableNumber >0 and c.seller.Id= :sellerId ORDER BY dataUpdateTime DESC" )
    Page<Coupon> findValidity(Pageable pageable,LocalDateTime curretTime,Integer sellerId);
    

    //根據賣家id搜尋賣家的優惠券
    @Query("select p.seller.coupons from ProductPage p where p.seller.id=:sellerId")
    List<Coupon>findCouponsBySellerId(@Param("sellerId")Integer sellerId);

   //搜尋優惠券，根據賣家ID、低消小於price、開始時間小於當前時間、結束時間大於當前時間、可使用數大於0
   @Query(value = "select * from coupon  where minium_spending_amount<=:price AND end_time> GETDATE() AND start_time< GETDATE() AND available_number>0 AND seller_id=:sellerId ",nativeQuery = true)
   List<Coupon>BestCoupons(@Param("price")Integer price,@Param("sellerId")Integer sellerId);

   
	Optional<Coupon> findByCode(String code);
   
   
}
