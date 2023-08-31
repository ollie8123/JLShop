package tw.com.jinglingshop.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.jinglingshop.model.dao.CreditcardRepository;
import tw.com.jinglingshop.model.dao.OrderDetailRepository;
import tw.com.jinglingshop.model.dao.OrderStatusRepository;
import tw.com.jinglingshop.model.dao.ProductPagePhotoRepository;
import tw.com.jinglingshop.model.domain.ProductReview;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;
import tw.com.jinglingshop.model.domain.order.Order;
import tw.com.jinglingshop.model.domain.order.OrderDetail;
import tw.com.jinglingshop.model.domain.order.OrderStatus;
import tw.com.jinglingshop.model.domain.order.ShoppingCart;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;
import tw.com.jinglingshop.model.domain.user.*;
import tw.com.jinglingshop.utils.emailUtil;
import tw.com.jinglingshop.utils.photoUtil;

import java.util.*;

/**
 * ClassName:CheckoutService
 * Package:tw.com.jinglingshop.service
 * Description:
 *
 * @Author chiu
 * @Create 2023/8/27 上午 01:24
 * @Version 1.0
 */
@Service
@Transactional
public class CheckoutService {
    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private SellerService sellerService;

    @Autowired
   private CreditcardRepository creditcardRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductPagePhotoService productPagePhotoService;
    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponDetailService couponDetailSave;

    @Autowired
    private  OrderStatusService orderStatusService;

    @Autowired
    private  OrderService orderService;

    @Autowired
    ProductPagePhotoRepository productPagePhotoRepository;


    public Map<String,Object> checkoutDetails(ArrayList<Integer> cartIds, String email) {

        Map<String,Object> res=new HashMap<>();
        List<ShoppingCart> shoppingCarts = shoppingCartService.selectShoppingCartByCartIds(cartIds);
        User user = userService.getUserByEmail(email);
        // res.put("shoppingCarts",shoppingCarts);
        //優惠券訊息
        ArrayList<HashMap<String, Object>> couponMsg = shoppingCartService.shoppingCartsBestCoupon(cartIds, email);
        for (int i=0;i<couponMsg.size();i++){
            System.out.println();
            ArrayList<HashMap<String,Object>> productList = new ArrayList<>();
            //遍歷商品列表
            for (ShoppingCart cartProduct:shoppingCarts) {
                //如果商品的賣家id等於couponMsg.get(i).get("sellerId")，將商品儲存進去
                if(cartProduct.getSeller().getId()==couponMsg.get(i).get("sellerId")){
                    HashMap<String, Object> product = new HashMap<>();
                    product.put("cartId",cartProduct.getId());
                    couponMsg.get(i).put("sellerName",cartProduct.getSeller().getStoreName());
                    //如果庫存大於等於購買量
                    if(cartProduct.getProduct().getStocks()>=cartProduct.getProductQuantity()){
                        product.put("excess","true");
                        product.put("cartProductQuantity",cartProduct.getProductQuantity());
                    }else {
                        product.put("excess","false");
                    }
                    product.put("productName",cartProduct.getProduct().getProductPage().getName());
                    product.put("productPrice",cartProduct.getProduct().getPrice());
                    product.put("productMainName", cartProduct.getProduct().getMainSpecificationClassOption().getName());
                    product.put("productSecondName", cartProduct.getProduct().getSecondSpecificationClassOption().getName());
                    if("~NoSecondSpecificationClass".equals(cartProduct.getProduct().getMainSpecificationClassOption().getClassName())){
                        ProductPagePhoto productPagePhoto = productPagePhotoService.selectProductPagePhotoProductSerialNumberByPageId(cartProduct.getProduct().getProductPage().getId());
                        //product.put("productPhoto",productPagePhoto.getPhotoPath());
                        product.put("productPhoto",photoUtil.getBase64ByPath(productPagePhoto.getPhotoPath()));
                    }else {
                       // product.put("productPhoto",cartProduct.getProduct().getMainSpecificationClassOption().getPhotoPath());
                        product.put("productPhoto", photoUtil.getBase64ByPath(cartProduct.getProduct().getMainSpecificationClassOption().getPhotoPath()));
                    }
                    productList.add(product);
                }
            }

            couponMsg.get(i).put("productList",productList);


        }
        res.put("sellerMsg",couponMsg);
        //使用者信用卡
        ArrayList<HashMap<String, Object>> userCreditCard = new ArrayList<>();
        List<Creditcard> creditCardList = creditcardRepository.findAllByUserId(user.getId());
        for (Creditcard c:creditCardList) {
            HashMap<String, Object> creditCardMap = new HashMap<>();
            creditCardMap.put("cardId",c.getId());
            creditCardMap.put("cardNumber",c.getCardNumber());
            userCreditCard.add(creditCardMap);
        }
        res.put("userCreditCard",userCreditCard);
        //回傳的地址資訊
        ArrayList<HashMap<String, Object>> recipientAddress = new ArrayList<>();
        //使用者超商地址
        List<ConvenienceStoreAddress> userStoreAddress = addressService.findUserStoreAddress(email);
        for (ConvenienceStoreAddress convenienceStoreAddress:userStoreAddress){
            HashMap<String, Object> storeAddHashMap = new HashMap<>();
            storeAddHashMap.put("storeAddressId",convenienceStoreAddress.getId());
            storeAddHashMap.put("name",  convenienceStoreAddress.getRecipientName());
            storeAddHashMap.put("phone",  convenienceStoreAddress.getRecipientPhone());
            if ("全家便利商店".equals(convenienceStoreAddress.getStoreType())){
                storeAddHashMap.put("type","FamilyMart");
                storeAddHashMap.put("storeName",  convenienceStoreAddress.getStoreName());
            }else {
                storeAddHashMap.put("type","7-11");
                storeAddHashMap.put("storeName",  convenienceStoreAddress.getStoreName());
            }
            storeAddHashMap.put("addressDetail",  convenienceStoreAddress.getAddressDetail());
            recipientAddress.add(storeAddHashMap);
        }
        //使用者一般地址
        List<NormalAddress> userAddress = addressService.findUserAddress(email);
        for (NormalAddress normalAddress:userAddress){
            HashMap<String, Object> normalAddressHashMap = new HashMap<>();
            normalAddressHashMap.put("normalAddressId",normalAddress.getId());
            normalAddressHashMap.put("name",normalAddress.getRecipientName());
            normalAddressHashMap.put("phone",normalAddress.getRecipientPhone());
            normalAddressHashMap.put("type","Normal");
            normalAddressHashMap.put("addressType",normalAddress.getAddressType());
            normalAddressHashMap.put("addressDetail",normalAddress.getAddressDetail());
            normalAddressHashMap.put("isDefault",normalAddress.getIsDefault());
            recipientAddress.add(normalAddressHashMap);
        }
        res.put("recipientAddress",recipientAddress);
        return res;
    }


    //新增訂單
    public String generateOrders(JSONObject object,String userEmail) throws Exception {
       //ArrayList<List<HashMap<String,Object>>> EmailArray = new ArrayList<>();
        //email賣家資訊陣列
        List<HashMap<String,Object>> emailMap = new ArrayList<>();
        int error=0;
        String msg=" ";
        User user = userService.getUserByEmail(userEmail);
        JSONArray sellerOrderList = object.getJSONArray("sellerOrderList");

        for(int i=0;i<sellerOrderList.length();i++){
            //email各賣家資訊
            HashMap<String, Object> sellerEmailMap = new HashMap<>();
            sellerEmailMap.put("userEmail",user.getEmail());
            //創建訂單
            Order order = new Order();
            //設定使用者
            order.setUser(user);
            JSONObject jsonObject = sellerOrderList.getJSONObject(i);
            int sellerId = jsonObject.getInt("sellerId");
            Seller seller = sellerService.selectSellerById(sellerId);
            if(seller!=null){
                //賣場名稱
                sellerEmailMap.put("seller",seller.getStoreName());

               //設定賣家
               order.setSeller(seller);
            }

            //付款方式
            String paymentMethod = object.getString("paymentMethod");
            //未付款
            if("貨到付款".equals(paymentMethod)){
                OrderStatus orderStatusById = orderStatusService.findOrderStatusById(2);
                if(orderStatusById!=null){
                    //設定訂單狀態
                    order.setOrderStatus(orderStatusById);
                }
            //以付款
            }else if("信用卡".equals(paymentMethod)){
                OrderStatus orderStatusById = orderStatusService.findOrderStatusById(2);
                if(orderStatusById!=null){
                //設定訂單狀態
                order.setOrderStatus(orderStatusById);
                }
                Integer card = object.getInt("card");
            }
            order.setAmount(0);
            Order order1 = orderService.saveOrder(order);
            //購物車商品陣列
            JSONArray productJSONArray= jsonObject.getJSONArray("productList");


            //信件商品陣列
            ArrayList<HashMap<String,Object>> emailSellerProductList = new ArrayList<>();
            int price=0;
            for (int j=0;j<productJSONArray.length();j++){
                //信件商品資訊
                HashMap<String, Object> emailProductMsg = new HashMap<>();
                //獲取cartId
               int cartId=productJSONArray.getJSONArray(j).getInt(0);
               //獲取購買數量
               int count =productJSONArray.getJSONArray(j).getInt(1);
                //根據id搜尋購物車
                ShoppingCart shoppingCart = shoppingCartService.selectShoppingCartById(cartId);
                //如果有找到
                if(shoppingCart!=null){
                    //如果庫存大於
                    if(shoppingCart.getProduct().getStocks()>=count){
                        //信件商品名稱
                        emailProductMsg.put("productName",shoppingCart.getProduct().getProductPage().getName());
                        //信件商品價格
                        emailProductMsg.put("productPrice",shoppingCart.getProduct().getPrice());
                        //信件商品數量
                        emailProductMsg.put("productCount",count);
                        //信件商品圖片
                        if("~NoSecondSpecificationClass".equals(shoppingCart.getProduct().getMainSpecificationClassOption().getClassName())){
                            ProductPagePhoto productPagePhoto = productPagePhotoRepository.selectProductPagePhotoProductSerialNumberByPageId(shoppingCart.getProduct().getProductPage().getId());
                           emailProductMsg.put("productPhoto",photoUtil.getBase64ByPath(productPagePhoto.getPhotoPath()));
                          //  emailProductMsg.put("productPhoto",productPagePhoto.getPhotoPath());
                        }else {
                            emailProductMsg.put("productPhoto",photoUtil.getBase64ByPath(shoppingCart.getProduct().getMainSpecificationClassOption().getPhotoPath()));
                           // emailProductMsg.put("productPhoto",shoppingCart.getProduct().getMainSpecificationClassOption().getPhotoPath());
                        }
                        //信件主規格名稱
                        emailProductMsg.put("productMainName", shoppingCart.getProduct().getMainSpecificationClassOption().getName());
                        //信件次規格名稱
                        emailProductMsg.put("productSecondName", shoppingCart.getProduct().getSecondSpecificationClassOption().getName());

                        //創建訂單明細
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order1);
                        orderDetail.setProduct(shoppingCart.getProduct());
                        orderDetail.setProductQuantity((short)count);
                        orderDetailRepository.save(orderDetail);
                        //扣除庫存
                        shoppingCart.getProduct().setStocks((short)(shoppingCart.getProduct().getStocks()-count));
                        //扣除購物車數量，如果扣除後為0則刪除
                        if(shoppingCart.getProductQuantity()-count==0){
                            if(!shoppingCartService.deleteShoppingCart(shoppingCart)){
                                error=1;
                                msg="刪除錯誤";
                            }
                         //扣除後有剩餘將根據購物車id更新數量為(當前數量-購買數量)
                        }else if (shoppingCart.getProductQuantity()-count>0){
                            shoppingCartService.updateShoppingCartByUserEmailAndShoppingCartId(cartId,shoppingCart.getProductQuantity()-count);
                        }else {
                            error=1;
                            msg=cartId+"購物車數量錯誤";
                        }
                    //查無此購物車
                    }else {
                        error=1;
                        msg="商品id"+shoppingCart.getProduct().getId()+"大於購買數量";
                    }
                    //總金額加總
                    price+=shoppingCart.getProduct().getPrice()*count;
                }else {
                    error=1;
                    msg=cartId+"購物車資訊";
                }
                emailSellerProductList.add(emailProductMsg);
            }
            //存入賣家商品
            sellerEmailMap.put("emailSellerProductList",emailSellerProductList);

            //優惠券id
            int couponId = jsonObject.getInt("couponId");
            //有優惠券
            if(couponId!=0){
                Coupon coupon = couponService.findById(couponId);
                //檢查可用張數大於使用張數
                if(coupon.getAvailableNumber()>coupon.getUsed()){
                    //判斷是否為趴數折扣
                    if(coupon.getDiscountRate()!=null){
                        //判斷最大折扣上限
                        if(coupon.getDiscountMaximum()!=null){
                            //如果扣除金額大於折扣上限
                            if( price-(Math.ceil(price*coupon.getDiscountRate()))>=coupon.getDiscountMaximum()){
                                //總價等於=總價-折扣上限
                                price=price-coupon.getDiscountMaximum();
                                sellerEmailMap.put("discountPrice",coupon.getDiscountMaximum());
                            }else {
                                //折扣價
                                int pri=(int)Math.ceil(price*coupon.getDiscountRate());
                                //總價=(原價*折扣去小數)
                                price=price-pri;
                                sellerEmailMap.put("discountPrice",pri);
                            }
                        }else {
                            error=1;
                            msg="優惠券折扣上限有誤";
                        }
                     //一般折扣
                    }else {
                        price=price-coupon.getDiscountAmount();
                        sellerEmailMap.put("discountPrice",coupon.getDiscountAmount());
                    }
                    //根據couponId跟使用者找出優惠券明細
                    CouponDetail couponDetail = couponService.findCouponDetailByUserIdAndCouponId(user.getId(), couponId);
                    couponDetail.setCouponCount((byte)( couponDetail.getCouponCount()-1));
                    couponDetail.getCoupon().setUsed(couponDetail.getCoupon().getUsed()+1);
                    if(!couponDetailSave.couponDetailSave(couponDetail)){
                        error=1;
                        msg="優惠券操作失敗";
                    }
                }else {
                    error=1;
                    msg="超過可用張數";
                }
            }else {
                sellerEmailMap.put("discountPrice",0);
                sellerEmailMap.put("coupon",null);
            }
            //訂單金額設定
            order1.setAmount(price);
            sellerEmailMap.put("orderPrice",price);

            JSONObject address = jsonObject.getJSONArray("address").getJSONObject(0);
            if("FamilyMart".equals(address.getString("type"))){
            //    int storeAddressId = jsonObject.getJSONArray("storeAddressId").getJSONObject(0).getInt("storeAddressId");
                //信件設置
                sellerEmailMap.put("name",address.get("name"));
                sellerEmailMap.put("phone",address.get("phone"));
                sellerEmailMap.put("storeName",address.get("storeName"));
                sellerEmailMap.put("addressDetail",address.get("addressDetail"));
                sellerEmailMap.put("type",address.get("type"));
        }else if("Normal".equals(address.getString("type"))){
              //  int anInt = jsonObject.getJSONArray("address").getJSONObject(0).getInt("normalAddressId");
                //信件設置
                sellerEmailMap.put("name",address.get("name"));
                sellerEmailMap.put("phone",address.get("phone"));
                sellerEmailMap.put("addressDetail",address.get("addressDetail"));
                sellerEmailMap.put("addressType",address.get("addressType"));
                sellerEmailMap.put("type",address.get("type"));
            }
            orderService.saveOrder(order1);
            emailMap.add(sellerEmailMap);
        }

        if (error==1){
            throw new Exception(msg);
        }else {
          //  System.out.println(emailMap);
          //  System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            emailUtil.testEmail(emailMap);
          //  System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
          // throw new Exception(msg);
        }
        return "新增成功";
    }

//    //根據購物車選取商品，回傳結帳頁面資訊
//    public void checkoutDetails(){
//
//
//    }
}
