package tw.com.jinglingshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.model.domain.user.BankAccount;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.service.BankAccountService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:7777")
public class BankAccountController {
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BankAccountService bankAccountService;
	
	
	
	@PostMapping("/createBankAccount")
	public Result createBankAccount(@RequestBody BankAccount bank,@CookieValue (name ="jwt")String cookieValue) {
		
		String email= JwtUtil.getUserEmailFromToken(cookieValue);

		
		User userdata = userService.getUserByEmail(email);
		bank.setUser(userdata);
		
		bankAccountService.addBankAccount(bank);
		
		
		return Result.success("success", bank);
	}
	

}
