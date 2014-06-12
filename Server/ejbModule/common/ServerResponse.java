package common;

import java.io.Serializable;

public class ServerResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// instance data
	private String title;
	private String content;
	
	public ServerResponse(String title, String content){
		this.setTitle(title);
		this.setContent(content);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setTitle(String title){
		this.title = title;
	}

	public void setContent(String content){
		this.content = content;
	}

}
