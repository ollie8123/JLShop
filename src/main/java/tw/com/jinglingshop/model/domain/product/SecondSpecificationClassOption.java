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
@Table(name = "[second_specification_class_option]")
public class SecondSpecificationClassOption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnore
	//product Table已經與page有關聯，此處是為了避免產生重複的page資訊
	@ManyToOne
	@JoinColumn
	private ProductPage productPage;

	private String className = "~NoSecondSpecificationClass";

	private String name = "~NoSpecification";

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
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "secondSpecificationClassOption")
	private List<Product> products;
}
