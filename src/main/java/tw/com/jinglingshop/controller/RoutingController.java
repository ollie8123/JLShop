package tw.com.jinglingshop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoutingController {
	@RequestMapping(path = { "/index.page", "/" })
	public String method1() {
		return "9999 ok";
	}
}
