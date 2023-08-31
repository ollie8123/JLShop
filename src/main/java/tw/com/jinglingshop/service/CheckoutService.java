package tw.com.jinglingshop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.jinglingshop.model.dao.CreditcardRepository;
import tw.com.jinglingshop.model.dao.OrderDetailRepository;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;
import tw.com.jinglingshop.model.domain.order.Order;
import tw.com.jinglingshop.model.domain.order.OrderDetail;
import tw.com.jinglingshop.model.domain.order.OrderStatus;
import tw.com.jinglingshop.model.domain.order.ShoppingCart;
import tw.com.jinglingshop.model.domain.product.ProductPagePhoto;
import tw.com.jinglingshop.model.domain.user.ConvenienceStoreAddress;
import tw.com.jinglingshop.model.domain.user.Creditcard;
import tw.com.jinglingshop.model.domain.user.NormalAddress;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.model.domain.user.User;
import tw.com.jinglingshop.utils.photoUtil;

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
        int error=0;
        String msg=" ";
        User user = userService.getUserByEmail(userEmail);
        JSONArray sellerOrderList = object.getJSONArray("sellerOrderList");
        for(int i=0;i<sellerOrderList.length();i++){
            //創建訂單
            Order order = new Order();
            //設定使用者
            order.setUser(user);
            JSONObject jsonObject = sellerOrderList.getJSONObject(i);
            int sellerId = jsonObject.getInt("sellerId");
            Seller seller = sellerService.selectSellerById(sellerId);
            if(seller!=null){
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
            int price=0;
            for (int j=0;j<productJSONArray.length();j++){
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
            }
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
                            if( price-(price*coupon.getDiscountRate())>coupon.getDiscountMaximum()){
                                //總價等於-折扣上限
                                price=price-coupon.getDiscountMaximum();
                            }else {
                                //折扣價格捨棄小數
                                price=(int)(price*coupon.getDiscountRate());
                            }
                        }else {
                            error=1;
                            msg="優惠券折扣上限有誤";
                        }
                     //一般折扣
                    }else {
                        price=price-coupon.getDiscountAmount();
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
            }
            //訂單金額設定
            order1.setAmount(price);

            //地址狀態
//            String type = jsonObject.getJSONArray("address").getJSONObject(0).getString("type");
//            if("FamilyMart".equals(type)){
//                int storeAddressId = jsonObject.getJSONArray("storeAddressId").getJSONObject(0).getInt("storeAddressId");
//                System.out.println("地址id"+storeAddressId);
//            }else {
//                int anInt = jsonObject.getJSONArray("address").getJSONObject(0).getInt("normalAddressId");
//                System.out.println("地址id"+anInt);
//            }
            orderService.saveOrder(order1);
            System.out.println("=======================================================");
        }
        if (error==1){
            throw new Exception(msg);
        }

        return "新增成功";
    }

//    //根據購物車選取商品，回傳結帳頁面資訊
//    public void checkoutDetails(){
//
//
//    }
}
