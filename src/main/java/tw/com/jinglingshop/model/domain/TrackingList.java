package tw.com.jinglingshop.model.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import tw.com.jinglingshop.model.domain.product.Product;
import tw.com.jinglingshop.model.domain.user.User;

@Data
@Entity
@Table(name = "[tracking_list]")
public class TrackingList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "trackingLists", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private User user;

	@JsonIgnoreProperties({ "trackingLists", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private Product product;

	@CreationTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataCreateTime;

}
