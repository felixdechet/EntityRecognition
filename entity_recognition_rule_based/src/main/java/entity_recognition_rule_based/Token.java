package entity_recognition_rule_based;

public class Token {
	
	private String token;
	private String tag;
	
	
	
	public Token(String token, String tag) {
		super();
		this.token = token;
		this.tag = tag;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

}
