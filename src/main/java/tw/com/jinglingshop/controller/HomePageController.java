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
	
	
	@PostMapping("/find40Products")
	public Result find40Products() {
		
		List<ProductPage>  productsList = homePageService.find20Products();
		
		List<Map<String, Object>> responseList = new ArrayList<>();
		
		for (ProductPage productPage : productsList) {
	        Map<String, Object> productMap = new HashMap<>();
	        productMap.put("product", productPage);
	        
	 
	        String image = homePageService.productsImg(productPage.getId());
	        productMap.put("image", image);
	        
	        Product product = homePageService.findProductInfo(productPage.getId());  // Assuming productPage.getId() provides productPageid
	        if (product != null) {
	            productMap.put("price", product.getPrice());
	            productMap.put("sales", product.getSales());
	        }
	        
	        
	        responseList.add(productMap);
	    }
		
		
		

		
		return Result.success("productsList", responseList);
		
	}
	
	
	
}
