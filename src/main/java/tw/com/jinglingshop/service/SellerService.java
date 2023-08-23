package tw.com.jinglingshop.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.SellerRepository;
import tw.com.jinglingshop.model.dao.UserRepository;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.model.domain.user.User;

@Service
public class SellerService {

	@Autowired
	private SellerRepository sellerRepository;
	
	@Autowired
	private UserRepository userRepository;

	public Seller addSellerAccount(Seller seller) {

		if (seller != null) {

			return sellerRepository.save(seller);

		}

		return null;

	}
	
	public String checkSellerAccount(String email) {
		
		Optional<User> existsEamil = userRepository.findByEmail(email);
		System.out.println("existsEamil="+existsEamil);
		
		if(existsEamil.isPresent()) {
			User user = existsEamil.get();
			
			Optional<Seller> sellerList  = sellerRepository.findByUserId(user.getId());
			
			if (!sellerList.isPresent()) {
				return "fail seller not found";
			}
			
			Seller seller= sellerList.get();

			Boolean sellerIsEnable= seller.getIsEnable();

			if(sellerIsEnable) {
				return "success";
			}
			return "fail IsEnable false";
			 
		}

		return "fail email is null";

	}
	
	
	

}
