package tw.com.jinglingshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.jinglingshop.model.dao.OrderRepository;
import tw.com.jinglingshop.model.domain.order.Order;
import org.springframework.transaction.annotation.Transactional;
/**
 * ClassName:OrderService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/29 上午 03:37
 * @Version 1.0
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;


    @Transactional
    public Order saveOrder(Order order){
        Order save;
        try {
            save = orderRepository.save(order);
        } catch (Exception e) {
            return null;
        }
        return save;

    }

}
