package tw.com.jinglingshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.service.SellerService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:7777")
public class SellerController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SellerService sellerService;
	
	
	@PostMapping("/createSeller")
	public Result createSeller(@RequestBody Seller seller,@CookieValue (name ="jwt")String cookieValue) {
		
		String email= JwtUtil.getUserEmailFromToken(cookieValue);

		
		User userdata = userService.getUserByEmail(email);
		seller.setUser(userdata);
		
		sellerService.addSellerAccount(seller);
		
		
		return Result.success("success", seller);
	}
	
	@PostMapping("/findSeller")
	public Result findSeller(@CookieValue (name ="jwt")String cookieValue) {
		

		String email= JwtUtil.getUserEmailFromToken(cookieValue);

		String isEnable = sellerService.checkSellerAccount(email);
		System.out.println("isEnable="+isEnable);
		
		if(isEnable.equals("success")) {
			return Result.success("success", isEnable);
			
		}
		return Result.error(isEnable);
		
		
	}
	
	
	

}
