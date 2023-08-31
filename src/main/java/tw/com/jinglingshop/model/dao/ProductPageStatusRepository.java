package tw.com.jinglingshop.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.product.ProductPageStatus;

public interface ProductPageStatusRepository extends JpaRepository<ProductPageStatus,Integer> {
    
}
