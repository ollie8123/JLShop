package tw.com.jinglingshop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.jinglingshop.model.dao.ProductPagePhotoRepository;
import tw.com.jinglingshop.model.domain.product.MainSpecificationClassOption;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;
import tw.com.jinglingshop.utils.photoUtil;

/**
 * ClassName:ProductPagePhotoService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/15 上午 11:23
 * @Version 1.0
 */
@Service
public class ProductPagePhotoService {

    @Autowired
    ProductPagePhotoRepository productPagePhotoRepository;



    //根據商品頁面搜尋第一張圖
    public ProductPagePhoto  selectProductPagePhotoProductSerialNumberByPageId(Integer pageId){
        return productPagePhotoRepository.selectProductPagePhotoProductSerialNumberByPageId(pageId);
    }


    //根據頁面搜尋所有相關圖片(頁面圖片+主類規格圖片)
    public ArrayList<HashMap<String,Object>> selectProductPageAllPhoto(Integer ProductPageId){
        ArrayList<HashMap<String,Object>> photoList = new ArrayList<>();
        ProductPage productPage = productPagePhotoRepository.selectProductPagePhotoByProductPageId(ProductPageId);
        List<ProductPagePhoto> productPagePhotos = productPage.getProductPagePhotos();
        for (ProductPagePhoto Photo:productPagePhotos) {
            HashMap<String,Object>hashMap=new HashMap<>();
            hashMap.put("type","page");
            //hashMap.put("photoPath",Photo.getPhotoPath());
           hashMap.put("photoPath",photoUtil.getBase64ByPath(Photo.getPhotoPath()));
            photoList.add(hashMap);
        }
        List<MainSpecificationClassOption> mains = productPage.getMainSpecificationClassOptions();
        System.out.println(mains.size());
        if(mains.size()>1){
            for (MainSpecificationClassOption main:mains) {
                HashMap<String,Object>hashMap=new HashMap<>();
                hashMap.put("type","main");
                // hashMap.put("photoPath",main.getPhotoPath());
                if(main.getPhotoPath()!=null){
                    hashMap.put("photoPath",photoUtil.getBase64ByPath(main.getPhotoPath()));
                }
                hashMap.put("mainId",main.getId());
                photoList.add(hashMap);
            }
        }

        return photoList;
    }
}
