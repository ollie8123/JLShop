package tw.com.jinglingshop.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.jinglingshop.model.domain.order.OrderStatus;

/**
 * ClassName:OrderStatusRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/29 上午 03:32
 * @Version 1.0
 */
public interface OrderStatusRepository extends JpaRepository<OrderStatus,Integer> {
}
