package tw.com.jinglingshop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.ProductPagePhotoRepository;
import tw.com.jinglingshop.model.dao.ProductPageRepository;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;
import tw.com.jinglingshop.utils.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName:ProductPageService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/7/30 上午 12:21
 * @Version 1.0
 */
@Service
public class ProductPageService {
    @Autowired
    private ProductPageRepository productPageRepository;

    //查詢商品頁面資料
    public HashMap<String,Object> selectProduct(Integer ProductPageId ){
        HashMap<String,Object> hashMap=new HashMap<>();
        //商品頁面資料
        Map<String,Object> productPage = productPageRepository.selectProductPageDetails(ProductPageId);
        hashMap.put("productPage",productPage);
        //搜尋主規格、第二規格名稱
        Map<String,String>specifications = productPageRepository.selectSecondAndMainSpecificationClassOptionNameByProductPageId(ProductPageId);
        hashMap.put("specifications",specifications);
        //判斷是否有主規格
        if (!"~NoSecondSpecificationClass".equals(specifications.get("main"))&&specifications.get("main").length()>0){
            //搜尋主規格細項、主規格資料處裡
            List<Map<String,Object>> mains = productPageRepository.selectMainSpecificationClassOptionByProductPageId(ProductPageId).stream()
                    .map(main->{
                        HashMap<String,Object>  mainHas= new HashMap<>();
                        mainHas.put("Id",main.getId());
                        mainHas.put("mainPhoto",main.getPhotoPath());
                        mainHas.put("name",main.getName());
                        return mainHas;
                    }).collect(Collectors.toList());
            hashMap.put("mains",mains);
            //判斷是否有第二規格
            if (!"~NoSecondSpecificationClass".equals(specifications.get("second"))&&specifications.get("second").length()>0){
                //搜尋第二規格細項、第二規格資料處裡
                List<Map<String,Object>> sends = productPageRepository.selectSecondSpecificationClassOptionByProductPageId(ProductPageId).stream()
                        .map(send->{
                            HashMap<String,Object>  sendHas= new HashMap<>();
                            sendHas.put("Id",send.getId());
                            sendHas.put("name",send.getName());
                            return sendHas;
                        }).collect(Collectors.toList());
                hashMap.put("sends",sends);
            }
        }
        return hashMap;
    }
    //關鍵字模糊搜尋
    public  List<String>  select(String select){
      return  productPageRepository.findByNameContaining(select);
    }

    //根據頁面獲取主類名稱，次類名稱、規格訊息
    public HashMap<String,Object> ProductPageSelectDetails(Integer productPageId){
        Optional<ProductPage> productPage = productPageRepository.findById(productPageId);
        if(productPage.isPresent()){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("mainProductCategory",productPage.get().getSecondProductCategory().getMainProductCategory().getName());
            hashMap.put("secondProductCategory",productPage.get().getSecondProductCategory().getName());
            hashMap.put("productDescription",productPage.get().getProductDescription());
            return hashMap;
        }else {
            return null;
        }
    }

    //關鍵字搜尋商品頁
    public Page<List<Map<String, Object>>> keywordSelectProductPages(String keyword, Pageable pageable, Optional<Integer> MinPrice, Optional<Integer> MaxPrice){
        Integer minPrice=MinPrice.isPresent()?MinPrice.get():0;
        Integer maxPrice=MaxPrice.isPresent()?MaxPrice.get():999999;
        Page<List<Map<String, Object>>> Pages = productPageRepository.keywordSelectProductPages(keyword,pageable,minPrice,maxPrice);
        return Pages;
    }
    //關鍵字+類別搜尋商品頁
    public  Page<List<Map<String, Object>>> keywordAndCategorySelectProductPages(String keyword, String type, Pageable pageable, Optional<Integer> MinPrice,Optional<Integer> MaxPrice){
        Integer minPrice=MinPrice.isPresent()?MinPrice.get():0;
        Integer maxPrice=MaxPrice.isPresent()?MaxPrice.get():999999;
        List<Integer> list = new ArrayList<>();
        String[] parts = type.split(",");
        for (String part : parts) {
            list.add(Integer.parseInt(part));
        }
        Page<List<Map<String, Object>>> Pages = productPageRepository.keywordAndCategorySelectProductPages(keyword,list,pageable,minPrice,maxPrice);
        return Pages;
    }

    //用頁面id找到賣家id，搜尋此賣家前5筆新增(不包含傳入頁面id)
    public ArrayList< Map<String, Object>> selectSellerTop5Product(Integer productPageId) {
        ArrayList< Map<String, Object>> resList = new ArrayList<>();
        Optional<ProductPage> ProductPage = productPageRepository.findById(productPageId);
        if(ProductPage.isPresent()){
            Integer sellerId = ProductPage.get().getSeller().getId();
            List<ProductPage> productPages = productPageRepository.selectSellerTop5ProductBySellerId(sellerId, productPageId, PageRequest.of(0, 5));
            for (ProductPage p:productPages){
                Map<String, Object> stringObjectMap = productPageRepository.selectProductPageDetails(p.getId());
                List<ProductPagePhoto> productPagePhotos = p.getProductPagePhotos();
                Optional<ProductPagePhoto> photoWithSerialNumberOne = productPagePhotos.stream()
                        .filter(photo -> photo.getSerialNumber()==1)
                        .findFirst();
                if(photoWithSerialNumberOne.isPresent()){
                    Map<String, Object> modifiableMap = new HashMap<>(stringObjectMap);
                    modifiableMap.put("photo",photoWithSerialNumberOne.get().getPhotoPath());
                    resList.add(modifiableMap);
                }
            }
        }
        return resList;
    }
}
