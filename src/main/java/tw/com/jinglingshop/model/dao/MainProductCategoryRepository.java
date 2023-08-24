package tw.com.jinglingshop.model.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.product.MainProductCategory;
import tw.com.jinglingshop.model.domain.product.MainSpecificationClassOption;

import java.util.List;
import java.util.Map;

/**
 * ClassName:MainProductCategoryRepository
 * Package:tw.com.jinglingshop.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/1 下午 08:39
 * @Version 1.0
 */
public interface MainProductCategoryRepository extends JpaRepository<MainProductCategory,Integer> {
    //搜尋類別名以及Id
    @Query("SELECT m.id,m.name from MainProductCategory m")
    List<String[]>selectMainProductCategory();

    //關鍵字+上架狀態搜尋，主類ID、主類類名、包含總數量
    @Query("SELECT p.secondProductCategory.mainProductCategory.id AS id, p.secondProductCategory.mainProductCategory.name AS category,COUNT (*) as count FROM ProductPage p WHERE p.name LIKE %:keyword% AND p.productPageStatus.id=2  GROUP BY p.secondProductCategory.mainProductCategory.name,p.secondProductCategory.mainProductCategory.id" )
    List<Map<String, Object>> findMainProductCategoryNameAndCount(@Param("keyword")String keyword);
}
