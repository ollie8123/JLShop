package tw.com.jinglingshop.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tw.com.jinglingshop.model.domain.order.Order;
import tw.com.jinglingshop.model.domain.order.OrderDetail;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.user.Creditcard;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.service.HomePageService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:7777")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private HomePageService homePageService;

	
	@PostMapping("/findUserByEmail")
	public Result findUserByEmail(@CookieValue (name ="jwt")String cookieValue ) {
	    try {
	    	String email= JwtUtil.getUserEmailFromToken(cookieValue);
	    	

	        User user = userService.getUserByEmail(email);
	        return Result.success("success", user);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Result.error("Error");
	    }
	}

	@PostMapping("/addUser")
	public Result addUser(@RequestBody User user) {
		System.out.println(user.getEmail());
		Boolean existsEamil = userService.findByEmail(user.getEmail());
		
		if (existsEamil) {
			return Result.error("此Email已被使用");

		}

		return userService.addUser(user);

	}
	@PutMapping("/addUserPhoto")
	public Result addUserPhtot(@RequestParam MultipartFile file,@CookieValue (name ="jwt")String cookieValue) {

		String email= JwtUtil.getUserEmailFromToken(cookieValue);
		userService.addPhoto(file, email);
		
		return Result.success("success");

	}

	
	@PutMapping("/updateUser")
	public Result updateUser(@RequestBody User user,@CookieValue (name ="jwt")String cookieValue ) {
		String email= JwtUtil.getUserEmailFromToken(cookieValue);
	
		return userService.updateUserInformation(user, email);

	}
	
	@RequestMapping(value = "/familyMartCallBack", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> callBack(@RequestParam("name") String name, @RequestParam("addr") String addr, HttpServletResponse response) {
	    
	    String decodedName = "";
	    String decodedAddr = "";
	    try {
	        
	        decodedName = URLDecoder.decode(name, "UTF-8");
	        decodedAddr = URLDecoder.decode(addr, "UTF-8");

	        System.out.println("Name: " + decodedName);
	        System.out.println("Addr: " + decodedAddr);

	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }

	    // Set cookies
	    Cookie storeNameCookie = new Cookie("storeName", decodedName);
	    Cookie addressDetailCookie = new Cookie("addressDetail", decodedAddr);
	    Cookie storeTypeCookie = new Cookie("storeType", "全家便利商店");
	    
	    // Set the expiration time for the cookies (optional, e.g., 24 hours here)
	    int oneDay = 60 * 60; // 24 hours in seconds
	    storeNameCookie.setMaxAge(oneDay);
	    addressDetailCookie.setMaxAge(oneDay);
	    storeTypeCookie.setMaxAge(oneDay);

	    // Add cookies to the response
	    response.addCookie(storeNameCookie);
	    response.addCookie(addressDetailCookie);
	    response.addCookie(storeTypeCookie);

	    String closeWindowScript = "<!DOCTYPE html><html><body><script type=\"text/javascript\">window.close();</script></body></html>";
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.TEXT_HTML);
	    return new ResponseEntity<>(closeWindowScript, headers, HttpStatus.OK);
	}
	

	

	
	@RequestMapping(value = "/callBack711", method = {RequestMethod.GET, RequestMethod.POST})
    public String callBack711(HttpServletRequest request) {

		System.out.println(request);
		

        return "Received";
    }
	
	@PostMapping("/createCreditcard")
	public Result createCreditcard (@RequestBody Creditcard user,@CookieValue (name ="jwt")String cookieValue) {
		
		
		String email= JwtUtil.getUserEmailFromToken(cookieValue);

		
		User userdata = userService.getUserByEmail(email);
		user.setUser(userdata);
		userService.addCreditcard(user);
		
		
		return Result.success("success", user);
	}
	
	
	@PostMapping("/findCreditcard")
	public Result findCreditcard (@CookieValue (name ="jwt")String cookieValue) {
		
		
		String email= JwtUtil.getUserEmailFromToken(cookieValue);
		
		
		List<Creditcard> creditcard = userService.findUserCreditcard(email);
			
		return Result.success("success", creditcard);
	}
	
	@PostMapping("/deleteCard")
	public Result deleteCard (@RequestBody Creditcard creditcard) {


		String deleteCreditcard = userService.deleteCreditcard(creditcard.getId());
		
		if(deleteCreditcard.equals("success")) {
			
			return Result.success("success");
		}else {
			return Result.error("fail");
		}
			
	}



	
	@PostMapping("/userPhoto")
    public ResponseEntity<String> userPhoto(@CookieValue (name ="jwt")String cookieValue) {
		 String email = JwtUtil.getUserEmailFromToken(cookieValue);
	        String base64Image = userService.loadImageAsResource(email);
	        if (base64Image == null) {
	        	
	        	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	        	
	        }
	        return ResponseEntity.ok(base64Image);
    }
	
	
	@PostMapping("/checkUserByEmail")
	public Result checkUserByEmail(@RequestBody User user ) {
	    try {
	    	String email = user.getEmail();
	    	

	        User userList = userService.getUserByEmail(email);
	        return Result.success("success", userList);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Result.error("Error");
	    }
	    
	    
	    
	    
	    
	}
	
	@PostMapping("/findUserOrder")
	public Result findUserOrder(@CookieValue(name ="jwt") String cookieValue) {
	    String email = JwtUtil.getUserEmailFromToken(cookieValue);

	    List<Order> orderList = userService.findUserOderList(email);

	    List<Map<String, Object>> customOrderInfos = new ArrayList<>();

	    for (Order order : orderList) {
	        List<OrderDetail> orderDetailList = userService.findUserOderDetailList(order.getId());
	        List<Map<String, Object>> productInfosForThisOrder = new ArrayList<>();

	        for (OrderDetail orderDetail : orderDetailList) {
	            Product product = userService.findProducts(orderDetail.getProduct().getId());
	            ProductPage productPage = product.getProductPage();

	            Map<String, Object> customProductInfo = new HashMap<>();
	            customProductInfo.put("productName", productPage.getName());
	            customProductInfo.put("storeName", productPage.getSeller().getStoreName());
	            customProductInfo.put("productQuantity", orderDetail.getProductQuantity());
	            customProductInfo.put("price", product.getPrice());
	            customProductInfo.put("productId", product.getId());
	            customProductInfo.put("productPageId", productPage.getId()); 

	            // 获取产品图片Base64字符串
	            String productImgBase64 = homePageService.productsImg(productPage.getId());
	            if(productImgBase64 != null) {
	                customProductInfo.put("productImg", productImgBase64);
	            }

	            productInfosForThisOrder.add(customProductInfo);
	        }

	        Map<String, Object> customOrderInfo = new HashMap<>();
	        customOrderInfo.put("orderId", order.getId());
	        customOrderInfo.put("products", productInfosForThisOrder);

	        customOrderInfos.add(customOrderInfo);
	    }

	    return Result.success("orderInfos", customOrderInfos);
	}
	
	

	
}

