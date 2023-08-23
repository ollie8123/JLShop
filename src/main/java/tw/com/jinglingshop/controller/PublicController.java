package tw.com.jinglingshop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.service.MainProductCategoryService;
import tw.com.jinglingshop.service.ProductPageService;
import tw.com.jinglingshop.utils.Result;

/**
 * ClassName:PublicController
 * Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/28 下午 05:48
 * @Version 1.0
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    MainProductCategoryService mainProductCategoryService;

    @Autowired
    ProductPageService productPageService;

    @PostMapping("/select")
    public Result indexSelect(@RequestBody Map<String,Object> aaa){
        List<String> select = productPageService.select((String) aaa.get("select"));
        return  Result.success(select);
    }

    @GetMapping("/keywordSelectProductList")
    public Result keywordSelectProductList(@Param("keyWord")String keyword,
                                           @RequestParam(value = "page", required = false) Optional<Integer> page,
                                           @RequestParam(value = "size", required = false) Optional<Integer> size,
                                           @RequestParam(value = "type", required = false)String type,
                                           @RequestParam(value = "MinPrice", required = false)Optional<Integer> MinPrice,
                                           @RequestParam(value = "MaxPrice", required = false)Optional<Integer> MaxPrice,
                                           @RequestParam(value = "evaluate", required = false)Optional<Integer> evaluate){
        List<Map<String, Object>> category = mainProductCategoryService.getAllMainProductCategory(keyword);
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("class",category);
        if(evaluate.isPresent()){
            System.out.println(evaluate.get());
        }
        //判斷頁碼
        Pageable pageable=(page.isPresent()&&size.isPresent())? PageRequest.of(page.get(),size.get()):PageRequest.of(0,5);
        //判斷主類
        if (type != null){
            if(!type.isEmpty()){
                System.out.println("類別判斷");
                Page<List<Map<String, Object>>> strings = productPageService.keywordAndCategorySelectProductPages(keyword,type,pageable,MinPrice,MaxPrice);
                objectObjectHashMap.put("products",strings);
                return  Result.success(objectObjectHashMap);
            }
        }
        System.out.println("直接判斷");
        Page<List<Map<String, Object>>> strings = productPageService.keywordSelectProductPages(keyword,pageable,MinPrice,MaxPrice);
        objectObjectHashMap.put("products",strings);
        return  Result.success(objectObjectHashMap);
    }


    //搜尋所有產品類型
    @PostMapping("/select/MainProductCategory")
    public  Result selectMainProductCategory(){
        return Result.success(mainProductCategoryService.selectMainProductCategory());
    }

    @RequestMapping("/productsSelect")
    @ResponseBody
    public Result productsSelect(@RequestBody Map<String,Object> aaa){
        List<String> select = productPageService.select((String) aaa.get("select"));
        List<Map<String, String>> suggestions = new ArrayList<>();
        for(String product:select){
            suggestions.add(Map.of("value",product));
        }
        return Result.success("成功",suggestions);
    }
}

