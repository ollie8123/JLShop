package tw.com.jinglingshop.model.domain.order;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.model.domain.user.User;

@Entity
@Data
@Table(name = "[order]")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @JsonIgnoreProperties({ "orders", "dataCreateTime", "dataUpdateTime" })
    @ManyToOne
    @JoinColumn
    private User user;

    @JsonIgnoreProperties({ "orders", "dataCreateTime", "dataUpdateTime" })
    @ManyToOne
    @JoinColumn
    private Seller seller;

    @JsonIgnoreProperties({ "orders", "dataCreateTime", "dataUpdateTime" })
    @ManyToOne
    @JoinColumn
    private OrderStatus orderStatus;

    private Integer amount;

    @CreationTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataCreateTime;

    @UpdateTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    // 序列化時僅可讀，不可寫入
    private LocalDateTime dataUpdateTime;

    /* 以下為資料表關聯(Table Relationship)的屬性宣告區 */

    @JsonIgnoreProperties({ "order", "dataCreateTime", "dataUpdateTime" })
    // 這邊這樣寫是希望可以從order取得所有order_detail的資料，是特例
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderDetail> orderDetails;
}
