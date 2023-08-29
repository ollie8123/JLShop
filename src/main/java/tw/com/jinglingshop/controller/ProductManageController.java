package tw.com.jinglingshop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.model.domain.product.SecondProductCategory;
import tw.com.jinglingshop.service.MainProductCategoryService;
import tw.com.jinglingshop.utils.Result;

@RestController
@RequestMapping(path = "/productManage")
public class ProductManageController {

    @Autowired
    MainProductCategoryService mainProductCategoryService;

    @GetMapping(path = "/getMainProductCategories")
    public Result getMainProductCategories() {
        
        try {
            List<Map<String, String>> resultData = new ArrayList<>();
            List<String[]> rawDatas = mainProductCategoryService.selectMainProductCategory();

            for (String[] rawData : rawDatas) {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("id", rawData[0]);
                hashMap.put("name", rawData[1]);
                resultData.add(hashMap);
            }

            return Result.success("Request [getMainProductCategories] Success!", resultData);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Request [getMainProductCategories] Fail!", e);
        }
    }
    @GetMapping(path = "getSecondCategoriesByMainCategoryId")
    public Result getSecondCategoriesByMainCategoryId(@RequestParam Integer id){
        List<SecondProductCategory> secondCategoriesByMainCategoryId = mainProductCategoryService.getSecondCategoriesByMainCategoryId(id);
        System.out.println(id);
        return Result.success("Success", secondCategoriesByMainCategoryId);
    }
}
