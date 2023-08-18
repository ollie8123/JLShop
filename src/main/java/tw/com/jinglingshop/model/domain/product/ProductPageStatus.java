package tw.com.jinglingshop.model.domain.product;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "[product_page_status]")
public class ProductPageStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "productPageStatus")
    private List<ProductPage> productPages;
}
