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
import tw.com.jinglingshop.model.domain.user.Seller;

@Entity
@Data
@Table(name = "[product_page]")
public class ProductPage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "productPages", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private Seller seller;

	@JsonIgnoreProperties({ "productPages", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private SecondProductCategory secondProductCategory;

	@JsonIgnoreProperties({ "productPages", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private ProductPageStatus productPageStatus;

	private String name;

	private String productDescription;

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
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "productPage")
	private List<Product> products;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "productPage")
	private List<MainSpecificationClassOption> mainSpecificationClassOptions;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "productPage")
	private List<SecondSpecificationClassOption> secondSpecificationClassOptions;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "productPage")
	private List<ProductPagePhoto> productPagePhotos;
}
