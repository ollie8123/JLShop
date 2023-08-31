package tw.com.jinglingshop.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.service.CouponService;
import tw.com.jinglingshop.service.MainProductCategoryService;
import tw.com.jinglingshop.service.ProductPagePhotoService;
import tw.com.jinglingshop.service.ProductPageService;
import tw.com.jinglingshop.service.ProductReviewService;
import tw.com.jinglingshop.service.ProductService;
import tw.com.jinglingshop.service.SellerService;
import tw.com.jinglingshop.service.UserService;
import tw.com.jinglingshop.utils.JwtUtil;
import tw.com.jinglingshop.utils.Result;

/**
 * ClassName:PublicController
 * Package:tw.com.jinglingshop.controller
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/28 下午 05:48
 * @Version 1.0
 */
@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    CouponService couponService;
    @Autowired
    ProductReviewService productReviewService;
    @Autowired
    ProductService productService;
    @Autowired
    MainProductCategoryService mainProductCategoryService;
    @Autowired
    SellerService sellerService;
    @Autowired
    ProductPageService productPageService;
    @Autowired
    ProductPagePhotoService productPagePhotoService;

    @Autowired
    UserService userService;

    //測試文件編輯器
    @PostMapping("testText")
    public void  testText(@RequestParam Map<String,Object> aaa){
        System.out.println( aaa);
    }

    //根據頁面id獲取賣家圖片
    @PostMapping("/productPageSellerPhoto")
    public ResponseEntity<String> userPhoto(@RequestBody HashMap<String,Integer> body) {
        System.out.println("進入");
        Integer ProductPageId = body.get("ProductPageId");
        System.out.println(ProductPageId);

        ProductPage productPage = productPageService.productPagefindById(ProductPageId);
        String base64Image = userService.loadImageAsResource(productPage.getSeller().getUser().getEmail());
        if (base64Image == null) {

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseEntity.ok(base64Image);
    }

    //獲取賣場商品前五筆最新商品
    @GetMapping("/SellerTop5Product")
    public Result SellerTop5Product(@Param("productPageId")Integer productPageId){
        ArrayList<Map<String, Object>> maps = productPageService.selectSellerTop5Product(productPageId);
        return Result.success(maps);
    }


    //搜尋頁面賣家的優惠券、如果有登入會獲取使用者優惠券
    @GetMapping("/SellerCoupon")
    public Result sellerCoupon(@Param("productPageId")Integer productPageId, HttpServletRequest httpServletRequest){
        String userEmail = JwtUtil.CookieGetEmail(httpServletRequest);
        return Result.success(couponService.findSellerCouponByPageId(productPageId,userEmail));
    }
    //搜尋商品頁面所有圖片(頁面圖片+主規格圖片)
    @GetMapping("/AllProductPagePhoto")
    public Result AllProductPagePhoto(@Param("productPageId")Integer productPageId){
        return Result.success( productPagePhotoService.selectProductPageAllPhoto(productPageId));
    }
    //商品頁面評論查詢
    @GetMapping("/SelectPageReview")
    public Result ProductPageReviewSelectByLevelAndPageId(@Param("productPageId")Integer productPageId,@Param("pageNum")Integer pageNum,@Param("level")Integer level){
        if(level!=null&&pageNum!=null&&productPageId!=null){
            return Result.success(productReviewService.selectProductPageAllReview(productPageId,level,pageNum));
        }else {
            return Result.error("參數錯誤");
        }
    }
    //商品頁面評論統計資料(平均評價，各評價數)
    @GetMapping("/ProductPageReview")
    public Result ProductPageReview(@Param("productPageId")Integer productPageId){
        return Result.success(productReviewService.selectProductPageAllReviewMsg(productPageId));
    }
    //商品頁面獲取規格訊息
    @GetMapping("/ProductPageSelectDetails")
    public Result ProductPageSelectDetails(@Param("productPageId")Integer productPageId){
        return  Result.success(   productPageService.ProductPageSelectDetails(productPageId));
    }
    //商品頁面獲取賣家資訊
    @GetMapping("/ProductPageSelectSeller")
    public Result ProductPageSelectSeller(@Param("productPageId")Integer productPageId){
        return  Result.success(sellerService.selectSellerInformation(productPageId));
    }
    //無規格頁面搜尋庫存
    @GetMapping("/ProductPageSelectProductStocks")
    public Result ProductPageSelectProductStocks(@Param("productPageId")Integer productPageId){
        try {
            return Result.success(productService.selectProductStocksByProductPageId(productPageId));
        }catch (Exception e){
            return Result.error("輸入資料誤");
        }
    }
    //只有主規格查詢
    //根據主規格查詢商品
    @GetMapping("/ProductPageSelectProductByMainId")
    public Result ProductPageSelectProductByMainId (@Param("mainId")Integer mainId){
        return Result.success(productService.selectProductByMainId(mainId));
    }
    //只有主規格
    //根據頁面id查詢商品沒有庫存的主規格id
    @GetMapping("/SelectNoStocksMainIdByProductPageId")
    public Result SelectProductNoStocksMainIdByProductPageId (@Param("productPageId")Integer productPageId){
        return Result.success(productService.SelectProductNoStocksMainIdByProductPageId(productPageId));
    }
    //雙規格查詢
    //根據主規格，次規格搜尋商品，並回傳以主規格搜尋沒有庫存的次規格id陣列，以及用次規格搜尋沒有庫存的主規格id陣列
    @GetMapping("/ProductPageSelectProduct")
    public Result ProductPageSelectProduct(@Param("mainId")Integer mainId,@Param("secondId")Integer secondId){
        if((mainId!=null&&secondId!=null)&&mainId>0&&secondId>0){
            return Result.success(productService.selectProductByMainIdAndSecondId(mainId, secondId));
        }else {
            return Result.error("輸入值有誤");
        }
    }
    //根據主規格或是次規格查詢，對應主規格，次規格沒有庫存的id陣列(如果輸入主規格就會回傳次規格庫存為0的id陣列)
    @GetMapping("/selectNoStockProduct")
    public Result selectNoStockProductBySecondId(@Param("mainId")Integer mainId,@Param("secondId")Integer secondId){
        if(mainId==null&&secondId==null){
            return Result.error("未輸入值");
        }
        return Result.success(productService.selectNoStockProduct(mainId,secondId));
    }
    //獲取單個商品頁面資訊
    @GetMapping("/selectProduct")
    public Result selectProduct(@Param("ProductPageId")Integer ProductPageId){
        HashMap<String, Object> HashMap = productPageService.selectProduct(ProductPageId);
        return Result.success(HashMap);
    }
    //根據關鍵字與條件搜尋商品(待修改)
    @GetMapping("/keywordSelectProductList")
    public Result keywordSelectProductList(@Param("keyWord")String keyword,
                                           @RequestParam(value = "page", required = false) Optional<Integer> page,
                                           @RequestParam(value = "size", required = false) Optional<Integer> size,
                                           @RequestParam(value = "type", required = false)String type,
                                           @RequestParam(value = "MinPrice", required = false)Optional<Integer> MinPrice,
                                           @RequestParam(value = "MaxPrice", required = false)Optional<Integer> MaxPrice,
                                           @RequestParam(value = "evaluate", required = false)Optional<Integer> evaluate,
                                           @RequestParam(value = "chronological", required = false)Optional<String> chronological
                                           ){
        List<Map<String, Object>> category = mainProductCategoryService.selectMainProductCategoryBykeyword(keyword);
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("class",category);
        keyword=keyword.replace(" ", "");
        if(keyword.isEmpty()){
            return  Result.success();
        }
        Pageable pageable=(page.isPresent()&&size.isPresent())? PageRequest.of(page.get(),size.get()):PageRequest.of(0,3);
        String sortOrder;
        if(chronological.isPresent()){
            if("ASC".equals(chronological.get())){
                sortOrder="ASC";
            }else{
                sortOrder="DESC";
            }
        }else {
            sortOrder="DESC";
        }
        //判斷主類
        if (type != null){
            if(!type.isEmpty()){
                System.out.println("類別判斷");
                HashMap<String, Object> hashMap = productPageService.keywordAndCategorySelectProductPages(keyword, type, pageable, MinPrice, MaxPrice, evaluate, sortOrder);
                objectObjectHashMap.put("products",hashMap);
                return  Result.success(objectObjectHashMap);
            }
        }
        System.out.println("直接判斷");
        HashMap<String, Object> hashMap = productPageService.keywordSelectProductPages(keyword, pageable, MinPrice, MaxPrice, evaluate, sortOrder);
        objectObjectHashMap.put("products",hashMap);
        return  Result.success(objectObjectHashMap);
    }
    //搜尋所有產品類型
    @PostMapping("/select/MainProductCategory")
    public  Result selectMainProductCategory(){
        return Result.success(mainProductCategoryService.selectMainProductCategory());
    }
    //關鍵字搜尋
    @RequestMapping("/productsSelect")
    @ResponseBody
    public Result productsSelect(@RequestBody Map<String,Object> keyword){
        List<String> select = productPageService.select((String) keyword.get("select"));
        List<Map<String, String>> suggestions = new ArrayList<>();
        for(String product:select){
            suggestions.add(Map.of("value",product));
        }
        return Result.success("成功",suggestions);
    }
}

