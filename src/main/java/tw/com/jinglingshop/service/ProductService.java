package tw.com.jinglingshop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.ProductPageRepository;
import tw.com.jinglingshop.model.dao.ProductRepository;
import tw.com.jinglingshop.model.dao.SellerRepository;
import tw.com.jinglingshop.model.domain.product.Product;

/**
 * ClassName:ProductService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/11 上午 03:26
 * @Version 1.0
 */
@Service
public class ProductService {

    @Autowired
    ProductPageRepository productPageRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SellerRepository sellerRepository;


    //根據主規格或次規格搜尋沒庫存的商品id
    public List<Integer> selectNoStockProduct(Integer mainId, Integer secondId) {
         if(mainId!=null&&mainId>0){
            return productRepository.selectNoStockByMainId(mainId);
         }else if(secondId!=null&&secondId>0){
             return productRepository.selectNoStockBySecondId(secondId);
         }
         return new ArrayList<>();
    }

    //根據主規格次規格搜尋商品、以及根據主規格找沒庫存的次規格商品id，在根據次規格找沒庫存的主規格商品id
    public HashMap<String, Object> selectProductByMainIdAndSecondId(Integer mainId, Integer secondId) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("product",productRepository.selectProductByMainIdAndSecondId(mainId, secondId));
        hashMap.put("NoStockSecond",productRepository.selectNoStockByMainId(mainId));
        hashMap.put("NoStockMain",productRepository.selectNoStockBySecondId(secondId));
        return  hashMap;
    }

    //只有主規格的情況下，根據主規格id搜尋搜尋(商品價錢、庫存售出數)
    public HashMap<String,Object> selectProductByMainId(Integer mainId){
        HashMap<String,Object>hashMap=new HashMap<>();
        Product product = productRepository.selectProductByMainId(mainId);
        hashMap.put("price",product.getPrice());
        hashMap.put("stocks",product.getStocks());
        hashMap.put("sales",product.getSales());
        return hashMap;
    }

    //只有主規格的情況下，根據ProductPageId搜尋主規格庫存為0的主規格id
    public List<Integer> SelectProductNoStocksMainIdByProductPageId(Integer ProductPageId){
        List<Integer> idList = new ArrayList<>();
        for (Product p: productRepository.findProductByProductPageIdAndStocksEquals(ProductPageId, 0)) {
            idList.add(p.getMainSpecificationClassOption().getId());
        }
        return idList;
    }

    //只有主規格
    //根據頁面id搜尋商品庫存
    public Integer  selectProductStocksByProductPageId(Integer productPageId) {
      return  productRepository.findStocksByProductPageId(productPageId);
    }

    public Product selectProductBy(Integer productPageId, Integer mainId, Integer secondId) {
        //主規格id=0代表沒有規格，表示也不會有次規格，所以只會找到一個商品
        Product product=null;
        if(mainId==0){
            List<Product> Product = productRepository.findByProductPageId(productPageId);
            if(Product.size()==1){
                product=Product.get(0);
            }
        //主規格id!=0，次規格id=0代表沒有次規格，主規格是唯一值所以可以直接用mainId找到商品
        }else if(secondId==0){
            product = productRepository.selectProductByMainId(mainId);
         //主規格id!=0，次規格id!=0
        }else{
            product = productRepository.selectProductByMainIdAndSecondId(mainId, secondId);
        }
        return product;
    }
}
