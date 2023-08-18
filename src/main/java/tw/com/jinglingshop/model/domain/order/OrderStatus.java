package tw.com.jinglingshop.model.domain.order;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "[order_status]")
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonValue
    private String name;

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
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "orderStatus")
    private List<Order> orders;
}
