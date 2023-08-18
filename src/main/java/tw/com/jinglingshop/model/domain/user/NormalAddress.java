package tw.com.jinglingshop.model.domain.user;

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
@Table(name = "[normal_address]")
public class NormalAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnoreProperties({ "normalAddresses", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private User user;

	private String recipientName;

	private String recipientPhone;

	private String county;

	private String postalCode;

	private String addressDetail;

	private String addressType;

	private Boolean isDefault;

	@CreationTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataCreateTime;

	@UpdateTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataUpdateTime;

}
