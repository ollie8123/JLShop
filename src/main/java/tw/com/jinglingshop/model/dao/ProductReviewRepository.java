package tw.com.jinglingshop.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.ProductReview;

import java.util.List;


/**
 * ClassName:ProductReviewRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/13 上午 02:56
 * @Version 1.0
 */
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    //根據商品頁面id找到賣家評價
    @Query("from ProductReview pr " +
           "join OrderDetail od on od.id=pr.orderDetail.id " +
           "JOIN Order o on o.id=od.order.id " +
           "JOIN ProductPage pdtp on pdtp.seller.id=o.seller.id " +
           "where pdtp.id=:ProductPagesId" )
    List<ProductReview>findSellerProductReviewByProductPageId(@Param("ProductPagesId")Integer ProductPagesId);

    //根據頁面id、評價等級、分頁，搜尋評價
    @Query("from ProductReview pr where pr.orderDetail.product.productPage.id=:ProductPagesId AND pr.level=:level")
    Page<ProductReview> findProductPageProductReviewByProductPageIdAndLevel(@Param("ProductPagesId")Integer ProductPagesId,@Param("level")Integer level , Pageable pageable);
    //根據頁面id、分頁
    @Query("from ProductReview pr where pr.orderDetail.product.productPage.id=:ProductPagesId")
    Page<ProductReview> findProductPageProductReviewByProductPageId(@Param("ProductPagesId")Integer ProductPagesId, Pageable pageable);
    //根據頁面id查詢所有相關評論
    @Query("from ProductReview pr where pr.orderDetail.product.productPage.id=:ProductPagesId")
    List<ProductReview> findProductPageProductReviewMsgByProductPageId(@Param("ProductPagesId")Integer ProductPagesId);

//    測試語句差異
//    根據頁面ID搜尋頁面相關商品評價
//    @Query("select pr.user.account as 使用者帳號 ," +
//            "pr.user.photoPath AS 使用者圖片 ," +
//            "pr.level AS 評價," +
//            "pr.Content AS 評論內容 ," +
//            "pr.orderDetail.product.mainSpecificationClassOption.id as 主規格ID ," +
//            "pr.orderDetail.product.mainSpecificationClassOption.className as 主規格類名 ," +
//            "pr.orderDetail.product.mainSpecificationClassOption.name as 主規格名," +
//            "pr.orderDetail.product.secondSpecificationClassOption.id as 次規格ID  ," +
//            "pr.orderDetail.product.secondSpecificationClassOption.className as 次規格類名 ," +
//            "pr.orderDetail.product.secondSpecificationClassOption.name as 次規格名 " +
//            "from  ProductReview pr " +
//            "join OrderDetail odt on odt.id=pr.orderDetail.id " +
//            "join Product p on p.id=odt.product.id " +
//            "join ProductPage pdpg on pdpg.id=p.productPage.id " +
//            "where pdpg.id=:ProductPagesId ")
//    List<Map<String,Object>>findProductPageProductReviewByProductPageId2(@Param("ProductPagesId")Integer ProductPagesId);

}
