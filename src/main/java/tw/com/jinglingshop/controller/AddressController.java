package tw.com.jinglingshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.jinglingshop.model.domain.user.ConvenienceStoreAddress;
import tw.com.jinglingshop.model.domain.user.NormalAddress;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.service.AddressService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:7777")
public class AddressController {

	@Autowired
	private AddressService addressService;

	@Autowired
	private UserService userService;

	@PostMapping("/createAddress")
	public Result createAddress(@RequestBody NormalAddress address, @CookieValue(name = "jwt") String cookieValue) {

		String email = JwtUtil.getUserEmailFromToken(cookieValue);

		User userdata = userService.getUserByEmail(email);
		address.setUser(userdata);

		addressService.addAddress(address);

		String allIsDefault = addressService.allIsDefault(email);

		if (allIsDefault.equals("success")) {

			String addressIsDefault = addressService.addressIsDefault(address.getId());

			if (addressIsDefault.equals("success")) {

				return Result.success("success");
			}
		}
		return Result.error("fail");
	}

	@PostMapping("/findAddress")
	public Result findAddress(@CookieValue(name = "jwt") String cookieValue) {

		String email = JwtUtil.getUserEmailFromToken(cookieValue);

		List<NormalAddress> address = addressService.findUserAddress(email);

		return Result.success("success", address);
	}

	@PostMapping("/deleteAddress")
	public Result deleteAddress(@RequestBody NormalAddress address) {

		String deleteAddress = addressService.deleteAddress(address.getId());

		if (deleteAddress.equals("success")) {

			return Result.success("success");
		} else {
			return Result.error("fail");
		}

	}

	@PostMapping("/updateAddressIsDefault")
	public Result updateAddressIsDefault(@RequestBody NormalAddress address,
			@CookieValue(name = "jwt") String cookieValue) {

		String email = JwtUtil.getUserEmailFromToken(cookieValue);
		String allIsDefault = addressService.allIsDefault(email);

		if (allIsDefault.equals("success")) {

			String addressIsDefault = addressService.addressIsDefault(address.getId());

			if (addressIsDefault.equals("success")) {

				return Result.success("success");
			}
		}
		return Result.error("fail");
	}

	@PostMapping("/createStoreAddress")
	public Result createStoreAddress(@RequestBody ConvenienceStoreAddress storeAddress,
			@CookieValue(name = "jwt") String cookieValue) {

		String email = JwtUtil.getUserEmailFromToken(cookieValue);

		User userdata = userService.getUserByEmail(email);
		storeAddress.setUser(userdata);

		addressService.addStoreAddress(storeAddress);

		return Result.success("success", storeAddress);
	}

	@PostMapping("/findStoreAddress")
	public Result findStoreAddress(@CookieValue(name = "jwt") String cookieValue) {

		String email = JwtUtil.getUserEmailFromToken(cookieValue);

		List<ConvenienceStoreAddress> storeAddress = addressService.findUserStoreAddress(email);

		return Result.success("success", storeAddress);
	}

	@PostMapping("/deleteStoreAddress")
	public Result deleteStoreAddress(@RequestBody ConvenienceStoreAddress storeAddress) {

		String deleteStoreAddress = addressService.deleteStoreAddress(storeAddress.getId());

		if (deleteStoreAddress.equals("success")) {

			return Result.success("success");
		} else {
			return Result.error("fail");
		}

	}

}
