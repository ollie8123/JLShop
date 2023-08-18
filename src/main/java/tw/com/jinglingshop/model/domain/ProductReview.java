package tw.com.jinglingshop.model.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import tw.com.jinglingshop.model.domain.order.OrderDetail;
import tw.com.jinglingshop.model.domain.user.User;

@Entity
@Data
@Table(name = "product_review")
public class ProductReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "productReview", "dataCreateTime", "dataUpdateTime" })
	@OneToOne
	@JoinColumn
	private OrderDetail orderDetail;

	@JsonIgnoreProperties({ "productReviews", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private User user;

	private Byte level;

	private String Content;

	@CreationTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataCreateTime;

	@UpdateTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataUpdateTime;

}
