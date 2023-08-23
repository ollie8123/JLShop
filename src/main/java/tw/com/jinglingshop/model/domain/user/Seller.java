package tw.com.jinglingshop.model.domain.user;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import tw.com.jinglingshop.model.domain.coupon.Coupon;
import tw.com.jinglingshop.model.domain.order.Order;
import tw.com.jinglingshop.model.domain.order.ShoppingCart;
import tw.com.jinglingshop.model.domain.product.ProductPage;

@Entity
@Data
@Table(name = "[seller]")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnoreProperties({ "seller", "dataCreateTime", "dataUpdateTime" })
    @OneToOne
    @JoinColumn
    private User user;

    private Boolean isEnable = true;
    
    private String storeName;
    
   
    @CreationTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataCreateTime;

    @UpdateTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataUpdateTime;

    /* 以下為資料表關聯(Table Relationship)的屬性宣告區 */
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "seller")
    private List<ProductPage> productPages;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "seller")
    // 不希忘賣家刪除就刪除訂單紀錄，因此此處使用MERGE
    private List<Order> orders;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "seller")
    private List<ShoppingCart> shoppingCarts;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "seller")
    private List<Coupon> coupons;

}
