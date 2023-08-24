package tw.com.jinglingshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.com.jinglingshop.model.dao.ProductReviewRepository;
import tw.com.jinglingshop.model.domain.ProductReview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:ProductReviewService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/14 上午 10:34
 * @Version 1.0
 */
@Service
public class ProductReviewService {
    @Autowired
    ProductReviewRepository productReviewRepository;

    //根據商品頁面id搜尋平均評價、1~5星評價
    public Map<String,Object>  selectProductPageAllReviewMsg(Integer ProductPageId) {
        Map<String, Object> remap = new HashMap<>();
        int[] levels = new int[5];
        double avg = 0;
        List<ProductReview> productReviewList = productReviewRepository.findProductPageProductReviewMsgByProductPageId(ProductPageId);
        for (ProductReview p : productReviewList) {
            avg += p.getLevel();
            switch (p.getLevel()) {
                case 1:
                    levels[0]++;
                    break;
                case 2:
                    levels[1]++;
                    break;
                case 3:
                    levels[2]++;
                    break;
                case 4:
                    levels[3]++;
                    break;
                case 5:
                    levels[4]++;
                    break;
            }
        }
        for (int i = 0; i < levels.length; i++) {
            remap.put("level" + (i + 1), levels[i]);
        }
        if (avg > 0) {
            avg = avg / productReviewList.size();
        }
        remap.put("avg", avg);
        remap.put("total", productReviewList.size());
        return remap;
    }
    //搜尋商品頁面評價(評價者帳號、評價者圖片、等級、主規格id及名稱、次規格id及名稱、評價內容、評價時間)
    public Map<String,Object>  selectProductPageAllReview(Integer ProductPageId,Integer level, Integer pageNum){
        Page<ProductReview> productReviews;
        if(level==0){
            productReviews= productReviewRepository.findProductPageProductReviewByProductPageId(ProductPageId,PageRequest.of(pageNum,5));
        }else {
            productReviews= productReviewRepository.findProductPageProductReviewByProductPageIdAndLevel(ProductPageId,level,PageRequest.of(pageNum, 5));
        }
        Map<String,Object> remap=new HashMap<>();
        List<Map<String, Object>> ProductReviewlist =new ArrayList<>();
        HashMap<String, Object> page = new HashMap<>();
        page.put("PageSize",productReviews.getSize());
        page.put("getTotalElements",productReviews.getTotalElements());
        remap.put("pageMsg",page);
        for (ProductReview pr:productReviews) {
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("userAccount", pr.getUser().getAccount());
            hashMap.put("userPhotoPath", pr.getUser().getPhotoPath());
            hashMap.put("level", pr.getLevel());
            hashMap.put("mainId", pr.getOrderDetail().getProduct().getMainSpecificationClassOption().getId());
            hashMap.put("mainClassName", pr.getOrderDetail().getProduct().getMainSpecificationClassOption().getClassName());
            hashMap.put("mainName", pr.getOrderDetail().getProduct().getMainSpecificationClassOption().getName());
            hashMap.put("secondId", pr.getOrderDetail().getProduct().getSecondSpecificationClassOption().getId());
            hashMap.put("secondClassName", pr.getOrderDetail().getProduct().getSecondSpecificationClassOption().getClassName());
            hashMap.put("secondName", pr.getOrderDetail().getProduct().getSecondSpecificationClassOption().getName());
            hashMap.put("content", pr.getContent());
            hashMap.put("createTime", pr.getDataCreateTime());
            ProductReviewlist.add(hashMap);
        }
        remap.put("ProductReviewList",ProductReviewlist);
        return  remap;

    }


}
