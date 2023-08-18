package tw.com.jinglingshop.model.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import lombok.Data;
import tw.com.jinglingshop.model.domain.user.User;

@Entity
@Data
@Table(name = "[chat_history]")
public class ChatHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnoreProperties({ "messageSenders", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private User sender;
	// 由於兩個屬性皆關聯至UserTable[id]，因此此處命名方式不同

	@JsonIgnoreProperties({ "messageRecipients", "dataCreateTime", "dataUpdateTime" })
	@ManyToOne
	@JoinColumn
	private User recipient;
	// 由於兩個屬性皆關聯至UserTable[id]，因此此處命名方式不同

	private String messageContent;

	private Boolean isReaded = false;

	@CreationTimestamp
	@JsonProperty(access = Access.READ_ONLY)
	// 序列化時僅可讀，不可寫入
	private LocalDateTime dataCreateTime;
	// 此Table不會有更新修改，因此沒有dataUpdateTime;
}
