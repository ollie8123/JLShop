package tw.com.jinglingshop.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.SecondProductCategory;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.service.MainProductCategoryService;
import tw.com.jinglingshop.service.ProductPageService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

@RestController
@RequestMapping(path = "/productManage")
public class ProductManageController {

    @Autowired
    MainProductCategoryService mainProductCategoryService;

    @Autowired
    ProductPageService productPageService;

    @Autowired
    UserService userService;

    @GetMapping(path = "/getMainProductCategories")
    public Result getMainProductCategories() {

        try {
            List<Map<String, String>> resultData = new ArrayList<>();
            List<String[]> rawDatas = mainProductCategoryService.selectMainProductCategory();

            for (String[] rawData : rawDatas) {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("id", rawData[0]);
                hashMap.put("name", rawData[1]);
                resultData.add(hashMap);
            }

            return Result.success("Request [getMainProductCategories] Success!", resultData);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Request [getMainProductCategories] Fail!", e);
        }
    }

    @GetMapping(path = "/getSecondCategoriesByMainCategoryId")
    public Result getSecondCategoriesByMainCategoryId(@RequestParam Integer id) {
        List<SecondProductCategory> secondCategoriesByMainCategoryId = mainProductCategoryService
                .getSecondCategoriesByMainCategoryId(id);
        System.out.println(id);
        return Result.success("Success", secondCategoriesByMainCategoryId);
    }

    @PostMapping(path = "/uploadProductPageData")
    public Result uploadProductPageData(@CookieValue(name = "jwt") String cookieValue,
            @RequestBody ProductPage unInsertData) {
        String email = JwtUtil.getUserEmailFromToken(cookieValue);
        if (email == "驗證過期" || email == "驗證失敗" || email == "資料錯誤") {
            return Result.error("User Not Loggin!");
        }
        Seller pageOwner = userService.getUserByEmail(email).getSeller();
        if (pageOwner == null) {
            return Result.error("Seller Not Exists!");
        }
        if (pageOwner.getIsEnable() == false) {
            return Result.error("This Seller is Disable!");
        }
        try {
            Integer pageId = productPageService.createProductPage(pageOwner, unInsertData).getId();
            return Result.success("Page Data Setting Success", pageId);
        } catch (Exception e) {
            return Result.error("Page Data Setting Fail", e);
        }
    }

    @PostMapping(path = "/uploadProducts")
    public Result uploadProducts(@CookieValue(name = "jwt") String cookieValue,
            @RequestParam Integer pageId,
            @RequestBody List<Product> products) {
        String email = JwtUtil.getUserEmailFromToken(cookieValue);
        if (email == "驗證過期" || email == "驗證失敗" || email == "資料錯誤") {
            return Result.error("User Not Loggin!");
        }
        try {
            productPageService.insertProducts(pageId, products);
            return Result.success("Page Data Setting Success");
        } catch (NoSuchElementException e) {
            return Result.error("Products Upload Fail because Page Not Exist", e);
        } catch (Exception e) {
            return Result.error("Page Data Setting Fail", e);
        }
    }

    @PostMapping(path = "/uploadProductPagePhotos")
    public Result uploadProductPagePhotos(@CookieValue(name = "jwt") String cookieValue,
            @RequestParam Integer pageId,
            @RequestParam("productPageImgs") MultipartFile[] files) {
        String email = JwtUtil.getUserEmailFromToken(cookieValue);
        if (email == "驗證過期" || email == "驗證失敗" || email == "資料錯誤") {
            return Result.error("User Not Loggin!");
        }
        try {
            productPageService.inserProductPageImgs(pageId, files);
            return Result.success("Product Page Photo Upload Success");
        } catch (NoSuchElementException e) {
            return Result.error("Imgs Upload Fail because Page is Not Exist", e);
        } catch (SQLException e) {
            return Result.error("Imgs Upload Fail for SQLException", e);
        } catch (IOException e) {
            return Result.error("Imgs Upload Fail for IOException", e);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Imgs Upload Fail", e);
        }
    }

    @PostMapping(path = "/uploadSpecPhotos")
    public Result uploadSpecPhotos(@CookieValue(name = "jwt") String cookieValue,
            @RequestParam Integer pageId,
            @RequestParam("productSpecImgs") MultipartFile[] files) {
        String email = JwtUtil.getUserEmailFromToken(cookieValue);
        if (email == "驗證過期" || email == "驗證失敗" || email == "資料錯誤") {
            return Result.error("User Not Loggin!");
        }
        try {
            productPageService.insertSpecificationImg(pageId, files);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(path = "/pageCreateDone")
    public Result pageCreateDone(@RequestParam Integer pageId, @RequestParam String pageStatus) {
        try {
            productPageService.pageStatusSetting(pageId, pageStatus);
            return Result.success("Product Page Create Success");
        } catch (SQLException e) {
            return Result.error("Page Status Setting Fail for SQLException", e);
        } catch (NullPointerException e) {
            return Result.error("[pageStatus] Param is not valid", e);
        } catch (NoSuchElementException e) {
            return Result.error("Page Status is Not Exist", e);
        }
    }
}
