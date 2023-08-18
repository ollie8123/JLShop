package tw.com.jinglingshop.model.domain;

import lombok.Data;

@Data
public class Emap {

	private String MerchantID;
	private String MerchantTradeNo;
	private String LogisticsType;
	private String LogisticsSubType;
	private String IsCollection;
	private String ServerReplyURL;
	private String ExtraData;
	private Integer Device;
	
	public String getDeviceAsString() {
        return (Device != null) ? Device.toString() : "0";  // 0 as default
    }
	

}
