package tw.com.jinglingshop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.service.ProductPageService;
import tw.com.jinglingshop.utils.Result;

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
