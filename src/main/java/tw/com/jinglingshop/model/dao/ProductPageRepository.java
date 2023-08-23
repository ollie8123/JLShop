package tw.com.jinglingshop.model.dao;

import java.util.List;
import java.util.Map;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.jinglingshop.model.domain.product.ProductPage;



/**
 * ClassName:ProductPageRepository
 * Package:tw.com.jinglingshop.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/30 上午 12:08
 * @Version 1.0
 */
public interface ProductPageRepository extends JpaRepository<ProductPage,Integer> {

    @Query("SELECT p.name FROM ProductPage p WHERE p.name LIKE %:keyWord%")
    List<String> findByNameContaining(@Param("keyWord")String keyWord);



    //關鍵字+productPageStatus.id=2(以上架狀態)+頁面設定，獲取商品ID,名稱,圖片,最大最小金額,評價平均
    @Query("SELECT p.id AS id,p.name AS name,pg.photoPath AS photoPath,MAX(pdt.price) AS maxPrice, MIN(pdt.price) AS minPrice,COALESCE(AVG(pr.level), 0) AS averageRating ,COALESCE(SUM(pdt.sales), 0) AS sales " +
            "FROM ProductPage p " +
            "JOIN ProductPagePhoto pg on p.id=pg.productPage.id " +
            "LEFT JOIN Product pdt on pdt.productPage.id=p.id " +
            "LEFT JOIN OrderDetail  od on od.product.id=pdt.id " +
            "Left JOIN ProductReview pr on pr.orderDetail.id=od.id " +
            "WHERE p.name LIKE %:keyWord% AND p.productPageStatus.id=2 AND pg.serialNumber=1 AND pdt.price>:minPrice AND pdt.price<:maxPrice " +
            "GROUP BY p.id,p.name,pg.photoPath")
    Page<List<Map<String, Object>>> keywordSelectProductPages(@Param("keyWord") String keyWord, Pageable pageable, @Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    //類別+關鍵字+productPageStatus.id=2(以上架狀態)+頁面設定，獲取商品ID,名稱,圖片,最大最小金額,評價平均
    @Query("SELECT p.id AS id,p.name AS name,pg.photoPath AS photoPath,MAX(pdt.price) AS maxPrice, MIN(pdt.price) AS minPrice,COALESCE(AVG(pr.level), 0) AS averageRating ,COALESCE(SUM(pdt.sales), 0) AS sales " +
            "FROM ProductPage p " +
            "JOIN ProductPagePhoto pg on p.id=pg.productPage.id " +
            "LEFT JOIN Product pdt on pdt.productPage.id=p.id " +
            "LEFT JOIN OrderDetail  od on od.product.id=pdt.id " +
            "Left JOIN ProductReview pr on pr.orderDetail.id=od.id " +
            "WHERE p.name LIKE %:keyWord% AND p.productPageStatus.id=2 AND pg.serialNumber=1 AND pdt.price>:minPrice AND pdt.price<:maxPrice " +
            "AND p.secondProductCategory.mainProductCategory.id IN (:idList)" +
            "GROUP BY p.id,p.name,pg.photoPath")
    Page<List<Map<String, Object>>> keywordAndCategorySelectProductPages(@Param("keyWord") String keyWord, @Param("idList") List<Integer> idList, Pageable pageable,@Param("minPrice") Integer minPrice,@Param("maxPrice") Integer maxPrice);
    //關鍵字+productPageStatus.id=2(以上架狀態)、根據主類ID搜尋商品，獲取頁面
    @Query("SELECT p.id,p.name,p.dataCreateTime,p.dataCreateTime FROM ProductPage p WHERE p.name LIKE %:keyWord% AND p.productPageStatus.id=2 AND p.secondProductCategory.mainProductCategory.id=:mainProductCategoryId" )
    List<Object[]> findProductPageByMainProductCategoryId(@Param("keyWord")String keyWord,Integer mainProductCategoryId);

}
