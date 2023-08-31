package tw.com.jinglingshop.model.domain.product;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import tw.com.jinglingshop.model.domain.TrackingList;
import tw.com.jinglingshop.model.domain.order.OrderDetail;
import tw.com.jinglingshop.model.domain.order.ShoppingCart;


@Entity
@Data
@Table(name = "[product]")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "products", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private ProductPage productPage;

	@JsonIgnoreProperties({ "products", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private MainSpecificationClassOption mainSpecificationClassOption;

	@JsonIgnoreProperties({ "products", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private SecondSpecificationClassOption secondSpecificationClassOption;

	private Integer price;

	private Short stocks;

	private Integer sales;

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
	@OneToMany(mappedBy = "product")
	private List<OrderDetail> orderDetails;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	private List<TrackingList> trackingLists;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	private List<ShoppingCart> shoppingCarts;
}
