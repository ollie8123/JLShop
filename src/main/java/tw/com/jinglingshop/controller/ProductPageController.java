package tw.com.jinglingshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.service.ProductPageService;

/**
 * ClassName:ProductPageController
 * Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/30 上午 12:20
 * @Version 1.0
 */
@RestController
public class ProductPageController {
    @Autowired
    ProductPageService service;



}
