package tw.com.jinglingshop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.ProductReviewRepository;
import tw.com.jinglingshop.model.dao.SellerRepository;
import tw.com.jinglingshop.model.dao.UserRepository;
import tw.com.jinglingshop.model.domain.ProductReview;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.model.domain.user.User;

/**
 * ClassName:SellerController
 * Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/13 上午 02:26
 * @Version 1.0
 */
@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
	private ProductReviewRepository productReviewRepository;

	@Autowired
	private UserRepository userRepository;

    //根據頁面id獲取賣家訊息(帳號、圖片、加入時間、商品數、評價數)
    public HashMap<String,Object> selectSellerInformation(Integer productPageId){
        Seller seller = sellerRepository.findSellerByProductPagesId(productPageId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("account",seller.getStoreName());
        hashMap.put("photoPath",seller.getUser().getPhotoPath());
        hashMap.put("dataCreateTime",seller.getUser().getDataCreateTime());
        hashMap.put("productCount", seller.getProductPages().size());
        List<ProductReview> productReviewByProductPageId = productReviewRepository.findSellerProductReviewByProductPageId(productPageId);
        hashMap.put("productReviewCount", productReviewByProductPageId.size());
        return hashMap;
    }
    public Seller selectSellerById(Integer sellerId){
		Optional<Seller> seller = sellerRepository.findById(sellerId);
		if(seller.isPresent()){
			return seller.get();
		}else {
			return null;
		}
	}


    public Seller addSellerAccount(Seller seller) {

		if (seller != null) {

			return sellerRepository.save(seller);

		}

		return null;

	}

	public String checkSellerAccount(String email) {

		Optional<User> existsEamil = userRepository.findByEmail(email);
		System.out.println("existsEamil=" + existsEamil);

		if (existsEamil.isPresent()) {
			User user = existsEamil.get();

			Optional<Seller> sellerList = sellerRepository.findByUserId(user.getId());

			if (!sellerList.isPresent()) {
				return "fail seller not found";
			}

			Seller seller = sellerList.get();

			Boolean sellerIsEnable = seller.getIsEnable();

			if (sellerIsEnable) {
				return "success";
			}
			return "fail IsEnable false";

		}

		return "fail email is null";

	}

}
