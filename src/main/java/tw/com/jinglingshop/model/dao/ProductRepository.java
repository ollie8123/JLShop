package tw.com.jinglingshop.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;

import java.util.List;

/**
 * ClassName:ProductRepository
 * Package:tw.com.jinglingshop.model.dao
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/10 下午 12:40
 * @Version 1.0
 */
public interface ProductRepository extends JpaRepository<Product,Integer> {

//    @Query("select pdt from Product pdt where pdt.productPage.id=:ProductPageId")
//    List<Product> selectProductByPageId(@Param("ProductPageId")Integer ProductPageId);

    //根據頁面ID查詢沒庫存的商品
    List<Product>findProductByProductPageIdAndStocksEquals(@Param("pageId")Integer pageId,@Param("stocks")Integer stocks);

    //根據主規格ID搜尋沒庫存的商品
    @Query("select p.secondSpecificationClassOption.id from  Product p where p.mainSpecificationClassOption.id=:MainId AND p.stocks=0")
    List<Integer>selectNoStockByMainId(@Param("MainId")Integer MainId);

    //根據次規格ID搜尋沒庫存的商品
    @Query("select p.mainSpecificationClassOption.id from  Product p where p.secondSpecificationClassOption.id=:SecondId AND p.stocks=0")
    List<Integer>selectNoStockBySecondId(@Param("SecondId")Integer SecondId);

    //根據主規格、次規格搜尋商品
    @Query("from  Product p where p.mainSpecificationClassOption.id=:MainId AND p.secondSpecificationClassOption.id=:SecondId")
    Product selectProductByMainIdAndSecondId(@Param("MainId")Integer MainId,@Param("SecondId")Integer SecondId);

    //根據主規格id搜尋商品
    @Query("from  Product p where p.mainSpecificationClassOption.id=:MainId")
    Product selectProductByMainId(@Param("MainId")Integer MainId);

    //只有主規格，根據頁面id搜尋商品庫存(多規格不可用)
    @Query("select p.stocks from Product  p where p.productPage.id=:pageId")
    Integer findStocksByProductPageId(@Param("pageId")Integer pageId);

    //頁面id搜尋商品
     List<Product> findByProductPageId(@Param("pageId")Integer pageId);

}
