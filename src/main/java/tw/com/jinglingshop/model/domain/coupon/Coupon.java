package tw.com.jinglingshop.model.domain.coupon;

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
import tw.com.jinglingshop.model.domain.user.Seller;

@Entity
@Data
@Table(name = "[coupon]")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnoreProperties({ "coupons", "dataCreateTime", "dataUpdateTime" })
    @ManyToOne
    @JoinColumn
    private Seller seller;

    private String name;

    private String code;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(columnDefinition = "DECIMAL(2,2)")
    private Float discountRate;

    private Short discountAmount;

    private Short miniumSpendingAmount;

    private Byte perPersonQuota;

    @CreationTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataCreateTime;

    @UpdateTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataUpdateTime;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "coupon")
    private List<CouponDetail> couponDetails;

}
