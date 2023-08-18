package tw.com.jinglingshop.model.domain.order;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import tw.com.jinglingshop.model.domain.ProductReview;
import tw.com.jinglingshop.model.domain.product.Product;

@Data
@Entity
@Table(name = "[orderDetail]")
public class OrderDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "orderDetails", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private Order order;

	@JsonIgnoreProperties({ "orderDetails", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private Product product;

	private Short productQuantity;

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
	@OneToOne(mappedBy="orderDetail", optional = true, fetch = FetchType.LAZY)
	private ProductReview productReview;
}
