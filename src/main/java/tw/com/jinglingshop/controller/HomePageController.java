package tw.com.jinglingshop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.service.HomePageService;
import tw.com.jinglingshop.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:7777")
public class HomePageController {

	@Autowired
	private HomePageService homePageService;
	
	
	@PostMapping("/find20Products")
	public Result find20Products() {
	    
	    List<ProductPage> productsList = homePageService.find20Products();
	    
	    List<Map<String, Object>> responseList = new ArrayList<>();
	    
	    for (ProductPage productPage : productsList) {
	        Map<String, Object> productMap = new HashMap<>();
	        productMap.put("product", productPage);
	        
	        String image = homePageService.productsImg(productPage.getId());
	        productMap.put("image", image);
	        
	        List<Product> productList = homePageService.findProductInfo(productPage.getId());  // Assuming productPage.getId() provides productPageid
	        if (productList != null && !productList.isEmpty()) {
	            if (productList.size() == 1) {
	                productMap.put("price", productList.get(0).getPrice());
	            } else {
	                // Get the minimum and maximum prices from the list
	                int minPrice = Integer.MAX_VALUE;
	                int maxPrice = Integer.MIN_VALUE;
	                
	                for (Product product : productList) {
	                    int price = product.getPrice();
	                    if (price < minPrice) {
	                        minPrice = price;
	                    }
	                    if (price > maxPrice) {
	                        maxPrice = price;
	                    }
	                }
	                
	                productMap.put("minPrice", minPrice);
	                productMap.put("maxPrice", maxPrice);
	            }
	            
	            // Assuming all the products in the list have the same sales, if different logic is required, please adjust
	            productMap.put("sales", productList.get(0).getSales());
	        }
	        
	        responseList.add(productMap);
	        
	        
	    }

	    System.out.println(responseList);
	    return Result.success("productsList", responseList);
	}
	
	
	
}
