package tw.com.jinglingshop.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.user.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer>{
    
}
