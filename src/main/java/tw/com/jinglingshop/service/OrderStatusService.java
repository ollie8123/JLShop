package tw.com.jinglingshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.jinglingshop.model.dao.OrderStatusRepository;
import tw.com.jinglingshop.model.domain.order.OrderStatus;

import java.util.Optional;

/**
 * ClassName:OrderStatusService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/29 上午 03:33
 * @Version 1.0
 */
@Service
public class OrderStatusService {

    @Autowired
    private OrderStatusRepository orderStatusRepository;


    public OrderStatus findOrderStatusById(Integer orderStatusId){
        Optional<OrderStatus> OrderStatus = orderStatusRepository.findById(orderStatusId);
        if (OrderStatus.isPresent()){
            return OrderStatus.get();
        }else {
            return null;
        }
    }
}
