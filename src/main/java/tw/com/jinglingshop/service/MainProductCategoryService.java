package tw.com.jinglingshop.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.MainProductCategoryRepository;
import tw.com.jinglingshop.model.domain.product.MainProductCategory;
import tw.com.jinglingshop.model.domain.product.SecondProductCategory;


/**
 * ClassName:MainProductCategoryService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/1 下午 08:40
 * @Version 1.0
 */
@Service
public class MainProductCategoryService {
    @Autowired
    private MainProductCategoryRepository mainProductCategoryRepository;

    //搜尋主類id與類名
    public List<String[]>  selectMainProductCategory(){
        return mainProductCategoryRepository.selectMainProductCategory();
    }

    //根據關鍵字獲取相關的類名與總數量
    public List<Map<String, Object>>  selectMainProductCategoryBykeyword(String keyword){
        List<Map<String, Object>> categorys = mainProductCategoryRepository.findMainProductCategoryNameAndCount(keyword);
        return categorys;
    }

    public List<SecondProductCategory>  getSecondCategoriesByMainCategoryId(Integer mainProductCategoryId){
        Optional<MainProductCategory> rawData = mainProductCategoryRepository.findById(mainProductCategoryId);

        if(rawData.isPresent()){
            List<SecondProductCategory> secondProductCategories = rawData.get().getSecondProductCategories();
            return secondProductCategories;
        }else{
            return null;
        }
    }
}
