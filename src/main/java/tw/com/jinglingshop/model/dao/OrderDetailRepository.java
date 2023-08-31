package tw.com.jinglingshop.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.jinglingshop.model.domain.order.OrderDetail;

/**
 * ClassName:OrderDetailRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/29 上午 02:23
 * @Version 1.0
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer> {
	
	List<OrderDetail> findByOrderId(Integer orderId);


}
