package tw.com.jinglingshop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.service.CouponService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

/**
 * ClassName:CouponController Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/16 下午 07:52
 * @Version 1.0
 */
@RestController
@CrossOrigin(origins = "http://localhost:7777")
public class CouponController {
	@Autowired
	private CouponService couponService;

	@PostMapping("/addCoupon")
	public Result addCoupon(@RequestParam Integer couponId, @CookieValue(name = "jwt") String cookieValue) {
		String email = JwtUtil.getUserEmailFromToken(cookieValue);
		String msg = couponService.addCoupon(couponId, email);
		return Result.success(msg);
	}

	// 新增優惠券
	@PostMapping("/insert")
	public Result insert(@RequestBody Coupon coupon, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			if (c.getName().equals("jwt")) {
				String email = JwtUtil.getUserEmailFromToken(c.getValue());
				System.out.println("email=" + email);
				Integer sellID = couponService.findSellerIdByEmail(email);
				System.out.println("sellerID:" + sellID);
				System.out.println("getSeller:" + coupon.getSeller());
				System.out.println("getID:" + coupon.getSeller().getId());
				Seller seller = new Seller();
				seller.setId(sellID);
				coupon.setSeller(seller);
				System.out.println("getID:" + coupon.getSeller().getId());

			}
			;
		}
		try {
			if (coupon.getDiscountAmount() != null && coupon.getDiscountRate() != null
					|| coupon.getDiscountAmount() == null && coupon.getDiscountRate() == null) {
				System.out.println("格式錯誤");
				return Result.error("資料格式錯誤");
			}
//			couponService.insert(coupon);
			else {
				Result success = Result.success(couponService.insert(coupon));
				System.out.println(success);
				return Result.success("新增成功", couponService.insert(coupon));

			}
		} catch (Exception e) {

			return Result.error();
		}
	}

	@DeleteMapping("/deleteById/{id}")
	public Result deleteById(@PathVariable Integer id) {
		couponService.deleteById(id);
		return Result.success();
	}

	@PostMapping("/edit")
	public Result editById(@RequestBody Coupon coupon) {
		System.out.println("coupon:"+coupon);
		try {
			Coupon finded = couponService.edit(coupon);
			return Result.success(finded);			
		}catch(Exception e) {
			System.out.println("errrrrrrrrrrrrrro");
			return Result.error();}
	}
	
	@PostMapping("/stop")
	public Result stopById(@RequestBody Coupon coupon) {
		
		try {
			Coupon finded = couponService.stop(coupon);
			return Result.success(finded);			
		}catch(Exception e) {
			System.out.println("errrrrrrrrrrrrrro");
			return Result.error();}
	}

	@GetMapping("/findById/{id}")
	public Result findById(@PathVariable Integer id) {
		return Result.success(couponService.findById(id));

	}
	
	@PostMapping("/findValidity")
	public Result findValidity(@RequestBody Map<String, Integer> datas, HttpServletRequest request) {
		Page<Coupon> page;
		Cookie[] cookies = request.getCookies();
		Integer sellID=0;
		for (Cookie c : cookies) {
			if (c.getName().equals("jwt")) {
				String email = JwtUtil.getUserEmailFromToken(c.getValue());
				System.out.println("email=" + email);
				 sellID = couponService.findSellerIdByEmail(email);
				System.out.println("sellerID:" + sellID);
//				System.out.println("getSeller:" + coupon.getSeller());
//				System.out.println("getID:" + coupon.getSeller().getId());
//				Seller seller = new Seller();
//				seller.setId(sellID);
//				coupon.setSeller(seller);
//				System.out.println("getID:" + coupon.getSeller().getId());

			}
			else{
				
			}}
		if (datas.get("rows") == 0) {

			
			page = couponService.getPaginatedData(datas.get("start"), Integer.MAX_VALUE,sellID);
		} else {

			page = couponService.getPaginatedData(datas.get("start"), datas.get("rows"),sellID);
		}
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("data", page.getContent());
		hashMap.put("total", page.getTotalElements());
		return Result.success(hashMap);

	}

	@PostMapping("/findExpired")
	public Result findExpired(@RequestBody Map<String, Integer> datas,HttpServletRequest request) {
		Page<Coupon> page;
		Cookie[] cookies = request.getCookies();
		Integer sellID=0;
		for (Cookie c : cookies) {
			if (c.getName().equals("jwt")) {
				String email = JwtUtil.getUserEmailFromToken(c.getValue());
				System.out.println("email=" + email);
				 sellID = couponService.findSellerIdByEmail(email);
				System.out.println("sellerID:" + sellID);}}
		if (datas.get("rows") == 0) {
			page = couponService.getPaginatedDataExpired(datas.get("start"), Integer.MAX_VALUE,sellID);
		} else {

			page = couponService.getPaginatedDataExpired(datas.get("start"), datas.get("rows"),sellID);
		}
		System.out.println(page.getTotalElements());
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("data", page.getContent());
		hashMap.put("total", page.getTotalElements());

		return Result.success(hashMap);

	}
	
	@PostMapping("/findNoVailbleNumber")
	public Result findNoVailbleNumber(@RequestBody Map<String, Integer> datas,HttpServletRequest request) {
		Page<Coupon> page;
		Integer sellID=0;
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			if (c.getName().equals("jwt")) {
				String email = JwtUtil.getUserEmailFromToken(c.getValue());
				System.out.println("email=" + email);
				 sellID = couponService.findSellerIdByEmail(email);
				System.out.println("sellerID:" + sellID);}}
		if (datas.get("rows") == 0) {
			page = couponService.getNoVailbleNumber(datas.get("start"), Integer.MAX_VALUE,sellID);
		} else {

			page = couponService.getNoVailbleNumber(datas.get("start"), datas.get("rows"),sellID);
		}
		System.out.println(page.getTotalElements());
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("data", page.getContent());
		hashMap.put("total", page.getTotalElements());

		return Result.success(hashMap);

	}

	

}
