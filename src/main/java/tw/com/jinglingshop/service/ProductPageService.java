package tw.com.jinglingshop.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.ProductPageRepository;

/**
 * ClassName:ProductPageService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/30 上午 12:21
 * @Version 1.0
 */
@Service
public class ProductPageService {
    @Autowired
    private ProductPageRepository productPageRepository;

    public  List<String>  select(String select){
      return  productPageRepository.findByNameContaining(select);
    }


    //關鍵字搜尋商品頁
    public Page<List<Map<String, Object>>> keywordSelectProductPages(String keyword, Pageable pageable, Optional<Integer> MinPrice, Optional<Integer> MaxPrice){
        Integer minPrice=MinPrice.isPresent()?MinPrice.get():0;
        Integer maxPrice=MaxPrice.isPresent()?MaxPrice.get():999999;
        Page<List<Map<String, Object>>> Pages = productPageRepository.keywordSelectProductPages(keyword,pageable,minPrice,maxPrice);
        return Pages;
    }


    //關鍵字+類別搜尋商品頁
    public  Page<List<Map<String, Object>>> keywordAndCategorySelectProductPages(String keyword, String type, Pageable pageable, Optional<Integer> MinPrice,Optional<Integer> MaxPrice){
        Integer minPrice=MinPrice.isPresent()?MinPrice.get():0;
        Integer maxPrice=MaxPrice.isPresent()?MaxPrice.get():999999;
        List<Integer> list = new ArrayList<>();
        String[] parts = type.split(",");
        for (String part : parts) {
            list.add(Integer.parseInt(part));
        }
        Page<List<Map<String, Object>>> Pages = productPageRepository.keywordAndCategorySelectProductPages(keyword,list,pageable,minPrice,maxPrice);
        return Pages;
    }
}
