package tw.com.jinglingshop.model.domain.product;

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

@Data
@Entity
@Table(name = "[main_product_category]")
public class MainProductCategory {

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

	@JsonIgnoreProperties({ "mainProductCategory", "dataCreateTime", "dataUpdateTime" })
	@ToString.Exclude
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "mainProductCategory")
	private List<SecondProductCategory> secondProductCategories;
}
