package ytc.com.modal;

public class EmailInfo {
	private String fromAddress;
	private String url;
	
	/** Begin - below variables will hold the details of the previous user which will be used in email body content.*/
	private String previousUserEmailId;
	private String previousUserName;
	/** End - Email body content .*/
	
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPreviousUserEmailId() {
		return previousUserEmailId;
	}
	public void setPreviousUserEmailId(String previousUserEmailId) {
		this.previousUserEmailId = previousUserEmailId;
	}
	public String getPreviousUserName() {
		return previousUserName;
	}
	public void setPreviousUserName(String previousUserName) {
		this.previousUserName = previousUserName;
	}
}
