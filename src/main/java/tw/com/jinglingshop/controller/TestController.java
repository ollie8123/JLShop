package tw.com.jinglingshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.model.dao.OrderRepository;
import tw.com.jinglingshop.model.dao.SellerRepository;
import tw.com.jinglingshop.model.dao.UserRepository;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.utils.Result;

@RestController
public class TestController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private SellerRepository sellerRepo;

    @GetMapping(value = "/test")
    public Result test() {
        return Result.success("Success", userRepo.findAll());
    }

    @GetMapping(path = "/test2")
    public Result test2(@RequestParam("id") Integer userId) {

        // orderRepo.findBySeller_Id(userId);
        // return Result.success("Success", orderRepo.findByUser_Id(userId));
        return Result.success(orderRepo.findByUserId(userId));
    }

    @PostMapping(path = "/test3")
    public Result test3(@RequestBody Seller seller) {
        seller.getUser().getId();
        return Result.success(sellerRepo.save(seller));
    }

    @GetMapping(value = "/test4")
    public Result test4() {
        return Result.success("Success", sellerRepo.findAll());
    }
}
