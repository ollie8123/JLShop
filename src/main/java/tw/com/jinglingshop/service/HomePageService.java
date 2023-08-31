package tw.com.jinglingshop.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.ProductPagePhotoRepository;
import tw.com.jinglingshop.model.dao.ProductPageRepository;
import tw.com.jinglingshop.model.dao.ProductRepository;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;

@Service
public class HomePageService {
	
	@Autowired
	private ProductPageRepository productPageRepository;
	
	@Autowired
	private ProductPagePhotoRepository productPagePhotoRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	
	public List<ProductPage> find20Products() {
		
		List<ProductPage>  productsList = productPageRepository.findAll();
		
		// 隨機排序產品列表
		Collections.shuffle(productsList);
				
		// 返回前40個，或者如果產品數量少於40，返回所有產品
		return productsList.stream().limit(20).collect(Collectors.toList());
		
	}
	
	public String productsImg(Integer productPageid) {
		try {
			
			Optional<ProductPagePhoto> existsProductPageId = productPagePhotoRepository.findByProductPageIdAndSerialNumber(productPageid,1);
			
	        if (existsProductPageId.isEmpty()) {
	            return null;
	        }

	        ProductPagePhoto productPagePhoto = existsProductPageId.get();
	        String photoPath = productPagePhoto.getPhotoPath();

	        if (photoPath == null) {
	            return null; // 如果filename為null，直接返回
	        }


	        Path file = Paths.get(photoPath);
	        byte[] fileBytes = Files.readAllBytes(file);
	        return Base64.getEncoder().encodeToString(fileBytes);
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
	    }

	 }
	
	
	public Product findProductInfo(Integer productPageid){
		
		Optional<Product>  productData= productRepository.findOneByProductPageId(productPageid);
		Product product = productData.get();
		
		return product;
		
	}
	
	

}
