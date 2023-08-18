package tw.com.jinglingshop.model.domain.product;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "[product_page_photo]")
public class ProductPagePhoto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "productPagePhotos", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private ProductPage productPage;

	private String photoPath;

	private Byte serialNumber;

	@CreationTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataCreateTime;

	@UpdateTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataUpdateTime;

}
