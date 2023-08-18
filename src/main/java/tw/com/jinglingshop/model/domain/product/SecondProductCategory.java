package tw.com.jinglingshop.model.domain.product;

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

@Data
@Entity
@Table(name = "[second_product_category]")
public class SecondProductCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "secondProductCategories", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private MainProductCategory mainProductCategory;

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
	@OneToMany( mappedBy = "secondProductCategory")
	// 不希望Category影響到Page的Table，此處不使用cascade
	private List<ProductPage> productPages;

}
