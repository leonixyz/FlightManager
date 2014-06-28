package common;

import java.io.Serializable;

/**
 * The server response containing all the data that the flight information
 * providing unit has got.
 * 
 * @author user
 * 
 */
public class ServerResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	// instance data
	private String title;
	private String content;

	/**
	 * Creates a ServerResponse
	 * 
	 * @param title
	 *            The title of the response
	 * @param content
	 *            The content of the response
	 * 
	 * @return ServerResponse
	 */
	public ServerResponse(String title, String content) {
		this.setTitle(title);
		this.setContent(content);
	}

	public String getTitle() {
		return this.title;
	}

	public String getContent() {
		return this.content;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
