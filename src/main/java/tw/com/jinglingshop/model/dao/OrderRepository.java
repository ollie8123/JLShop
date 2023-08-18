package tw.com.jinglingshop.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.order.Order;


public interface OrderRepository extends JpaRepository<Order, Integer>{
    
    List<Order> findByUserId(Integer userId);
    List<Order> findBySeller_Id(Integer SellerId);

}
