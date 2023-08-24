package tw.com.jinglingshop.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.user.Seller;


public interface SellerRepository extends JpaRepository<Seller, Integer>{

	
	Optional<Seller> findByUserId(Integer userId);
    

   //根據頁面搜尋賣家
   Seller findSellerByProductPagesId(@Param("ProductPagesId")Integer ProductPagesId);


}
