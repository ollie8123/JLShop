package tw.com.jinglingshop.model.domain.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import tw.com.jinglingshop.model.domain.ChatHistory;
import tw.com.jinglingshop.model.domain.ProductReview;
import tw.com.jinglingshop.model.domain.TrackingList;
import tw.com.jinglingshop.model.domain.coupon.CouponDetail;
import tw.com.jinglingshop.model.domain.order.Order;
import tw.com.jinglingshop.model.domain.order.ShoppingCart;

@Entity
@Data
@Table(name = "[user]")
// user是SQL Server的保留字，因此這邊必須特別指定name為[user]才能正常送出SQL指令
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String account;

    @JsonProperty(access = Access.WRITE_ONLY)
    // 序列化時僅寫入，不可讀取
    private String password;

    private String name;

    private String email;

    private LocalDate birth;

    private String photoPath;

    private String phone;

    private Boolean isEnable = true;

    @CreationTimestamp
    // 實體創建時自動產生時間的值
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataCreateTime;

    @UpdateTimestamp
    // 實體更新時自動更新時間的值
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataUpdateTime;

    /* 以下為資料表關聯(Table Relationship)的屬性宣告區 */

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "user", optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Seller seller;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "user", optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Admin admin;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "user")
    // 不希望使用者刪除就刪除訂單紀錄，因此此處使用MERGE
    private List<Order> orders;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<TrackingList> trackingLists;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ShoppingCart> shoppingCarts;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ProductReview> productReviews;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sender")
    private List<ChatHistory> messageSenders;
    // 由於兩個屬性皆關聯至ChatHistory，因此此處命名方式不同

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipient")
    private List<ChatHistory> messageRecipients;
    // 由於兩個屬性皆關聯至ChatHistory，因此此處命名方式不同

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<CouponDetail> couponDetails;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<BankAccount> bankAccounts;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ConvenienceStoreAddress> convenienceStoreAddresses;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Creditcard> creditcards;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<NormalAddress> normalAddresses;
}
