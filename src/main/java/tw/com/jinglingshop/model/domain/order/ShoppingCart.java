package tw.com.jinglingshop.model.domain.order;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.user.Seller;
import tw.com.jinglingshop.model.domain.user.User;

@Entity
@Data
@Table(name = "[shopping_cart]")
public class ShoppingCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "shoppingCarts", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private User user;

	@JsonIgnoreProperties({ "shoppingCarts", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private Seller seller;

	@JsonIgnoreProperties({ "shoppingCarts", "dataCreateTime", "dataUpdateTime" })
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

}
