package tw.com.jinglingshop.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tw.com.jinglingshop.config.DataPath;
import tw.com.jinglingshop.model.dao.ProductPageRepository;
import tw.com.jinglingshop.model.dao.ProductPageStatusRepository;
import tw.com.jinglingshop.model.domain.product.MainSpecificationClassOption;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.product.ProductPage;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;
import tw.com.jinglingshop.model.domain.product.ProductPageStatus;
import tw.com.jinglingshop.model.domain.product.SecondSpecificationClassOption;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.utils.photoUtil;

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
    DataPath dataPath;

    @Autowired
    private ProductPageRepository productPageRepository;

    @Autowired
    private ProductPageStatusRepository productPageStatusRepository;

    public ProductPage productPagefindById(Integer productPageId) {
        Optional<ProductPage> productPage = productPageRepository.findById(productPageId);
        if (productPage.isPresent()) {
            return productPage.get();
        } else {
            return null;
        }
    }

    // 查詢商品頁面資料
    public HashMap<String, Object> selectProduct(Integer ProductPageId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        // 商品頁面資料
        Map<String, Object> productPage = productPageRepository.selectProductPageDetails(ProductPageId);
        Map<String, Object> newProductPage = new HashMap<>(productPage);
        Map<String, Object> sales = productPageRepository.selectProductPageSalesById(ProductPageId);
        newProductPage.put("sales", sales.get("sales"));
        hashMap.put("productPage", newProductPage);
        // 搜尋主規格、第二規格名稱
        Map<String, String> specifications = productPageRepository
                .selectSecondAndMainSpecificationClassOptionNameByProductPageId(ProductPageId);
        hashMap.put("specifications", specifications);
        // 判斷是否有主規格
        if (!"~NoSecondSpecificationClass".equals(specifications.get("main"))
                && specifications.get("main").length() > 0) {
            // 搜尋主規格細項、主規格資料處裡
            List<Map<String, Object>> mains = productPageRepository
                    .selectMainSpecificationClassOptionByProductPageId(ProductPageId).stream()
                    .map(main -> {
                        HashMap<String, Object> mainHas = new HashMap<>();
                        mainHas.put("Id", main.getId());
                        mainHas.put("mainPhoto", main.getPhotoPath());
                        mainHas.put("name", main.getName());
                        return mainHas;
                    }).collect(Collectors.toList());
            hashMap.put("mains", mains);
            // 判斷是否有第二規格
            if (!"~NoSecondSpecificationClass".equals(specifications.get("second"))
                    && specifications.get("second").length() > 0) {
                // 搜尋第二規格細項、第二規格資料處裡
                List<Map<String, Object>> sends = productPageRepository
                        .selectSecondSpecificationClassOptionByProductPageId(ProductPageId).stream()
                        .map(send -> {
                            HashMap<String, Object> sendHas = new HashMap<>();
                            sendHas.put("Id", send.getId());
                            sendHas.put("name", send.getName());
                            return sendHas;
                        }).collect(Collectors.toList());
                hashMap.put("sends", sends);
            }
        }
        return hashMap;
    }

    // 關鍵字模糊搜尋
    public List<String> select(String select) {
        return productPageRepository.findByNameContaining(select);
    }

    // 根據頁面獲取主類名稱，次類名稱、規格訊息
    public HashMap<String, Object> ProductPageSelectDetails(Integer productPageId) {
        Optional<ProductPage> productPage = productPageRepository.findById(productPageId);
        if (productPage.isPresent()) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("mainProductCategory",
                    productPage.get().getSecondProductCategory().getMainProductCategory().getName());
            hashMap.put("secondProductCategory", productPage.get().getSecondProductCategory().getName());
            hashMap.put("productDescription", productPage.get().getProductDescription());
            return hashMap;
        } else {
            return null;
        }
    }

    // 關鍵字搜尋商品頁
    public HashMap<String, Object> keywordSelectProductPages(String keyword, Pageable pageable,
            Optional<Integer> MinPrice, Optional<Integer> MaxPrice, Optional<Integer> evaluate, String sortOrder) {
        Integer minPrice = MinPrice.isPresent() ? MinPrice.get() : 0;
        Integer maxPrice = MaxPrice.isPresent() ? MaxPrice.get() : 999999;
        Integer level = evaluate.isPresent() ? evaluate.get() : 0;

        Page<List<Map<String, Object>>> Pages = productPageRepository.keywordSelectProductPages(keyword, pageable,
                minPrice, maxPrice, level, sortOrder);
        JSONArray jsonArray = new JSONArray(Pages);
        HashMap<String, Object> resHashMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> map = new HashMap<String, Object>();
            for (String key : jsonObject.keySet()) {
                if (key.equals("photoPath")) {
                    map.put(key, photoUtil.getBase64ByPath((String) jsonObject.get(key)));
                } else if (key.equals("sales")) {
                    Map<String, Object> id = productPageRepository
                            .selectProductPageSalesById((Integer) jsonObject.get("id"));
                    map.put(key, id.get("sales"));
                } else {
                    Object value = jsonObject.get(key);
                    map.put(key, value);
                }
            }
            list.add(map);
        }
        resHashMap.put("content", list);
        resHashMap.put("totalElements", Pages.getTotalElements());
        resHashMap.put("pageSize", Pages.getPageable().getPageSize());
        System.out.println(list);
        return resHashMap;
    }

    // 關鍵字+類別搜尋商品頁
    public HashMap<String, Object> keywordAndCategorySelectProductPages(String keyword, String type, Pageable pageable,
            Optional<Integer> MinPrice, Optional<Integer> MaxPrice, Optional<Integer> evaluate, String sortOrder) {
        Integer minPrice = MinPrice.isPresent() ? MinPrice.get() : 0;
        Integer maxPrice = MaxPrice.isPresent() ? MaxPrice.get() : 999999;
        Integer level = evaluate.isPresent() ? evaluate.get() : 0;
        List<Integer> list = new ArrayList<>();
        String[] parts = type.split(",");
        for (String part : parts) {
            list.add(Integer.parseInt(part));
        }
        Page<List<Map<String, Object>>> Pages = productPageRepository.keywordAndCategorySelectProductPages(keyword,
                list, pageable, minPrice, maxPrice, level, sortOrder);
        JSONArray jsonArray = new JSONArray(Pages);
        HashMap<String, Object> resHashMap = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> map = new HashMap<String, Object>();
            System.out.println(map);
            for (String key : jsonObject.keySet()) {
                if (key.equals("photoPath")) {
                    map.put(key, photoUtil.getBase64ByPath((String) jsonObject.get(key)));
                } else if (key.equals("sales")) {
                    Map<String, Object> id = productPageRepository
                            .selectProductPageSalesById((Integer) jsonObject.get("id"));
                    map.put(key, id.get("sales"));
                } else {
                    Object value = jsonObject.get(key);
                    map.put(key, value);
                }
            }
            newList.add(map);
        }
        System.out.println(newList);
        resHashMap.put("content", newList);
        resHashMap.put("totalElements", Pages.getTotalElements());
        resHashMap.put("pageSize", Pages.getPageable().getPageSize());

        return resHashMap;
    }

    // 用頁面id找到賣家id，搜尋此賣家前5筆新增(不包含傳入頁面id)
    public ArrayList<Map<String, Object>> selectSellerTop5Product(Integer productPageId) {
        ArrayList<Map<String, Object>> resList = new ArrayList<>();
        Optional<ProductPage> ProductPage = productPageRepository.findById(productPageId);
        if (ProductPage.isPresent()) {
            Integer sellerId = ProductPage.get().getSeller().getId();
            List<ProductPage> productPages = productPageRepository.selectSellerTop5ProductBySellerId(sellerId,
                    productPageId, PageRequest.of(0, 5));
            for (ProductPage p : productPages) {
                Map<String, Object> stringObjectMap = productPageRepository.selectProductPageDetails(p.getId());
                Map<String, Object> newProductPage = new HashMap<>(stringObjectMap);
                Map<String, Object> sales = productPageRepository.selectProductPageSalesById(p.getId());
                newProductPage.put("sales", sales.get("sales"));

                List<ProductPagePhoto> productPagePhotos = p.getProductPagePhotos();
                Optional<ProductPagePhoto> photoWithSerialNumberOne = productPagePhotos.stream()
                        .filter(photo -> photo.getSerialNumber() == 1)
                        .findFirst();
                if (photoWithSerialNumberOne.isPresent()) {
                    Map<String, Object> modifiableMap = new HashMap<>(newProductPage);
                    modifiableMap.put("photo",
                            photoUtil.getBase64ByPath(photoWithSerialNumberOne.get().getPhotoPath()));
                    resList.add(modifiableMap);
                }
            }
        }
        return resList;
    }

    public ProductPage findProductPageById(Integer id) throws Exception {
        Optional<ProductPage> result = productPageRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    public ProductPage createProductPage(Seller pageOwner, ProductPage pageData) throws Exception {

        pageData.setSeller(pageOwner);

        // 先將頁面狀態設為關閉
        ProductPageStatus defaultStatus = new ProductPageStatus();
        defaultStatus.setId(1);
        pageData.setProductPageStatus(defaultStatus);
        // 若沒有規格則將規格設為預設值
        List<MainSpecificationClassOption> mainSpecOptions = pageData.getMainSpecificationClassOptions();
        if (mainSpecOptions == null) {
            MainSpecificationClassOption defaultSpecification = new MainSpecificationClassOption();
            ArrayList<MainSpecificationClassOption> defaultList = new ArrayList<>();
            defaultList.add(defaultSpecification);
            pageData.setMainSpecificationClassOptions(defaultList);
        } else {
            Iterator<MainSpecificationClassOption> iterator = mainSpecOptions.iterator();
            while (iterator.hasNext()) {
                MainSpecificationClassOption option = iterator.next();
                if (option.getClassName().isBlank() || option.getClassName() == null) {
                    option.setClassName("規格一");
                }
                if (option.getName() == null || option.getName().isBlank())
                    iterator.remove();
            }
        }
        List<SecondSpecificationClassOption> secondSpecOptions = pageData.getSecondSpecificationClassOptions();
        if (secondSpecOptions == null) {
            SecondSpecificationClassOption defaultSpecification = new SecondSpecificationClassOption();
            ArrayList<SecondSpecificationClassOption> defaultList = new ArrayList<>();
            defaultList.add(defaultSpecification);
            pageData.setSecondSpecificationClassOptions(defaultList);
        } else {
            Iterator<SecondSpecificationClassOption> iterator = secondSpecOptions.iterator();
            while (iterator.hasNext()) {
                SecondSpecificationClassOption option = iterator.next();
                if (option.getClassName() == null || option.getClassName().isBlank()) {
                    option.setClassName("規格二");
                }
                if (option.getName() == null || option.getName().isBlank()) {
                    if (secondSpecOptions.size() > 1) {
                        iterator.remove();
                    } else {
                        SecondSpecificationClassOption temp = new SecondSpecificationClassOption();
                        option.setName(temp.getName());
                        option.setClassName(temp.getClassName());
                    }
                }
            }
        }
        return productPageRepository.save(pageData);
    }

    public void insertProducts(Integer pageId, List<Product> products) throws Exception {

        ProductPage currentPage = productPageRepository.findById(pageId)
                .orElseThrow(() -> new NoSuchElementException("page id:" + pageId + " is not exist"));
        List<MainSpecificationClassOption> mainOptions = currentPage.getMainSpecificationClassOptions();
        List<SecondSpecificationClassOption> secondOptions = currentPage.getSecondSpecificationClassOptions();
        for (Product product : products) {
            product.setSales(0);
            if (product.getMainSpecificationClassOption() == null) {
                product.setMainSpecificationClassOption(mainOptions.get(0));
            } else {
                for (MainSpecificationClassOption option : mainOptions) {
                    if (option.getName().equals(product.getMainSpecificationClassOption().getName())) {
                        product.setMainSpecificationClassOption(option);
                    }
                }
            }
            if (product.getSecondSpecificationClassOption() == null) {
                product.setSecondSpecificationClassOption(secondOptions.get(0));
            } else {
                for (SecondSpecificationClassOption option : secondOptions) {
                    if (option.getName().equals(product.getSecondSpecificationClassOption().getName())) {
                        product.setSecondSpecificationClassOption(option);
                    } else if (product.getSecondSpecificationClassOption().getName() == null) {
                        product.setSecondSpecificationClassOption(secondOptions.get(0));
                    } 
                }
            }
        }

        currentPage.setProducts(products);
        productPageRepository.save(currentPage);
    }

    public void inserProductPageImgs(Integer pageId, MultipartFile[] files)
            throws SQLException, IOException, Exception {

        String uploadPath = dataPath.getPageImgsPath(pageId);
        // 若不存在資料夾就先建立
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        ProductPage currentPage = productPageRepository.findById(pageId)
                .orElseThrow(() -> new NoSuchElementException("page id:" + pageId + " is not exist"));
        ArrayList<ProductPagePhoto> pagePhotos = new ArrayList<>();
        currentPage.setProductPagePhotos(pagePhotos);

        Byte serialNumber = 1;
        for (MultipartFile file : files) {
            // 取得副檔名
            String oringinalFileName = file.getOriginalFilename();
            String extension = "";
            if (oringinalFileName != null) {
                extension = oringinalFileName.substring(oringinalFileName.lastIndexOf('.') + 1);
                if (extension != null) {
                    extension = "." + extension;
                }
            }
            // 產生單一檔案的路徑
            String filePath = uploadPath + UUID.randomUUID().toString() + extension;
            // 儲存檔案至檔案系統
            file.transferTo(new File(filePath));

            // 建立 ProductPagePhoto 實體並組合成List
            ProductPagePhoto photo = new ProductPagePhoto();
            photo.setPhotoPath(filePath);
            photo.setSerialNumber(serialNumber);
            photo.setProductPage(currentPage);
            serialNumber++;
            pagePhotos.add(photo);
            // 將路徑寫進資料庫中
            productPageRepository.save(currentPage);
            System.out.println("File written successfully: " + filePath);
        }
    }

    public void insertSpecificationImg(Integer pageId, MultipartFile[] files) throws IOException {
        String uploadPath = dataPath.getSpecImgsPath(pageId);

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        ProductPage currentPage = productPageRepository.findById(pageId)
                .orElseThrow(() -> new NoSuchElementException("page id:" + pageId + " is not exist"));
        List<MainSpecificationClassOption> mainSpecList = currentPage.getMainSpecificationClassOptions();
        int fileNum = 0;
        for (MultipartFile file : files) {
            // 取得副檔名
            String oringinalFileName = file.getOriginalFilename();
            String extension = "";
            if (oringinalFileName != null) {
                extension = oringinalFileName.substring(oringinalFileName.lastIndexOf('.') + 1);
                if (extension != null) {
                    extension = "." + extension;
                }
            }
            // 產生單一檔案的路徑
            String filePath = uploadPath + UUID.randomUUID().toString() + extension;
            // 儲存檔案至檔案系統
            file.transferTo(new File(filePath));
            // 更改規格實體
            MainSpecificationClassOption currentOption = mainSpecList.get(fileNum);
            currentOption.setPhotoPath(filePath);
            fileNum++;
        }
        productPageRepository.save(currentPage);
    }

    public void pageStatusSetting(Integer pageId, String pageStatusCommand) throws SQLException, NullPointerException {
        ProductPage currentPage = productPageRepository.findById(pageId)
                .orElseThrow(() -> new NoSuchElementException("page id:" + pageId + " is not exist"));
        Integer pageStatusId = null;
        if ("launch".equals(pageStatusCommand)) {
            pageStatusId = 2;
        }
        if ("unLaunch".equals(pageStatusCommand)) {
            pageStatusId = 1;
        }
        final Integer finalPageStatusId = pageStatusId;
        ProductPageStatus finalStatus = productPageStatusRepository.findById(finalPageStatusId)
                .orElseThrow(() -> new NoSuchElementException("page id:" + finalPageStatusId + " is not exist"));
        currentPage.setProductPageStatus(finalStatus);
        productPageRepository.save(currentPage);
    }
}
