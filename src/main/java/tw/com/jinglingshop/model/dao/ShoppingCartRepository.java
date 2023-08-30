package tw.com.jinglingshop.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.order.ShoppingCart;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ClassName:ShoppingCartRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/18 上午 01:37
 * @Version 1.0
 */
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Integer> {


     Optional<ShoppingCart> findAllByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);


     //查詢賣家id、帳號、最後更新時間，根據最後更新時間排序
     @Query("select s.seller.id AS id,s.seller.storeName AS sellerName ,s.seller.user.account AS account ,Max(s.dataUpdateTime) as latest_update_time " +
             "from  ShoppingCart s where s.user.id=:userId " +
             "group by s.seller.id,s.seller.user.account,s.seller.storeName order by latest_update_time DESC ")
     Page<Map<String,Object>> findAllSellerIdByUserId(@Param("userId") Integer userId, Pageable pageable);


     //根據用戶id搜尋用戶購物車
     List<ShoppingCart> findAllByUserId(@Param("userId") Integer userId);

     //根據userId、sellerId搜尋購物車並根據更新時間排序
     @Query(" from ShoppingCart s where s.user.id=:userId AND s.seller.id=:sellerId ORDER BY s.dataUpdateTime DESC")
     List<ShoppingCart> findAllByUserIdAndSellerId(@Param("userId") Integer userId,@Param("sellerId") Integer sellerId);


     Optional<ShoppingCart>findById(@Param("shoppingCartId") Integer shoppingCartId);


     @Modifying
     @Query(value = "delete FROM ShoppingCart s where s.id IN :ids")
     void deleteShoppingCartsByIds(@Param("ids") List<Integer> ids);

     //根據購物車ID搜尋賣家ID並群組化
     @Query("SELECT s.seller.id FROM ShoppingCart s where s.id IN:CartIds group by s.seller.id")
     List<Integer> selectShoppingCartSellerIdByCartIdsGroupSellerId(@Param("CartIds") List<Integer> CartIds);

     //根據多id查詢多筆ShoppingCart
     @Query("FROM ShoppingCart s where s.id IN:CartIds")
     List<ShoppingCart> selectShoppingCartByCartIds(@Param("CartIds") List<Integer> CartIds);
}
