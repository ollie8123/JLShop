package tw.com.jinglingshop.model.dao;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.product.MainSpecificationClassOption;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.SecondSpecificationClassOption;

import java.util.Optional;

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

    //關鍵字模糊搜尋
    @Query("SELECT p.name FROM ProductPage p WHERE p.name LIKE %:keyWord%")
    List<String> findByNameContaining(@Param("keyWord")String keyWord);

    Optional<ProductPage> findById(@Param("pageId")Integer pageId);


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
            "LEFT JOIN ProductReview pr on pr.orderDetail.id=od.id " +
            "WHERE p.name LIKE %:keyWord% AND p.productPageStatus.id=2 AND pg.serialNumber=1 AND pdt.price>:minPrice AND pdt.price<:maxPrice " +
            "AND p.secondProductCategory.mainProductCategory.id IN (:idList)" +
            "GROUP BY p.id,p.name,pg.photoPath")
    Page<List<Map<String, Object>>> keywordAndCategorySelectProductPages(@Param("keyWord") String keyWord, @Param("idList") List<Integer> idList, Pageable pageable,@Param("minPrice") Integer minPrice,@Param("maxPrice") Integer maxPrice);

    //商品頁面訊息(名稱、最大最小金額、平均評價、總銷量)
    @Query("SELECT p.id AS id,p.name AS name,MAX(pdt.price) AS maxPrice, MIN(pdt.price) AS minPrice,COALESCE(AVG(pr.level), 0) AS averageRating ,COALESCE(SUM(pdt.sales), 0) AS sales  " +
            "from ProductPage p " +
            "LEFT JOIN Product pdt on pdt.productPage.id=p.id " +
            "LEFT JOIN OrderDetail od on od.product.id=pdt.id " +
            "LEFT JOIN ProductReview  pr on pr.orderDetail.id=od.id " +
            "where p.id=:pageId AND p.productPageStatus.id=2 GROUP BY p.id,p.name ")
    Map<String,Object> selectProductPageDetails(@Param("pageId")Integer pageId);
    //根據頁面id搜尋主頁第二規格細項
    @Query(" from  SecondSpecificationClassOption s where s.productPage.id=:pageId ")
    List<SecondSpecificationClassOption> selectSecondSpecificationClassOptionByProductPageId(@Param("pageId")Integer pageId);

    //根據頁面id搜尋主規格細項
    @Query(" from  MainSpecificationClassOption m where m.productPage.id=:pageId ")
    List<MainSpecificationClassOption> selectMainSpecificationClassOptionByProductPageId(@Param("pageId")Integer pageId);

    //根據頁面id查詢主規格，第二規格名稱
    @Query("select m.className as main,s.className as second from  MainSpecificationClassOption m left join SecondSpecificationClassOption s on m.productPage.id=s.productPage.id where m.productPage.id=:pageId group by m.className,s.className")
    Map<String,String> selectSecondAndMainSpecificationClassOptionNameByProductPageId(@Param("pageId")Integer pageId);

    //關鍵字+productPageStatus.id=2(以上架狀態)、根據主類ID搜尋商品，獲取頁面
    @Query("SELECT p.id,p.name,p.dataCreateTime,p.dataCreateTime FROM ProductPage p WHERE p.name LIKE %:keyWord% AND p.productPageStatus.id=2 AND p.secondProductCategory.mainProductCategory.id=:mainProductCategoryId" )
    List<Object[]> findProductPageByMainProductCategoryId(@Param("keyWord")String keyWord,Integer mainProductCategoryId);

    //根據賣家搜尋新上架的商品頁面，不包含當前的
    @Query("FROM ProductPage p where p.seller.id=:sellerId AND p.id <> :exceptId  order by p.dataCreateTime desc")
    List<ProductPage>selectSellerTop5ProductBySellerId(@Param("sellerId")Integer sellerId,@Param("exceptId")Integer exceptId,Pageable pageable);

}
