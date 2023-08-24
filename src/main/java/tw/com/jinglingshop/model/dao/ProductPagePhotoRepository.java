package tw.com.jinglingshop.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;

import java.util.List;

/**
 * ClassName:ProductPagePhotoRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/15 上午 11:22
 * @Version 1.0
 */
public interface ProductPagePhotoRepository extends JpaRepository<ProductPagePhoto,Integer> {
    //根據頁面id搜尋商品頁面圖片
    @Query("select p from  ProductPage p where p.id=:pageId ")
    ProductPage selectProductPagePhotoByProductPageId(@Param("pageId")Integer pageId);

    //根據頁面id搜尋商品頁面圖片第一張圖
    @Query(" from  ProductPagePhoto p where p.productPage.id=:pageId AND p.serialNumber=1 ")
    ProductPagePhoto selectProductPagePhotoProductSerialNumberByPageId(@Param("pageId")Integer pageId);

}
