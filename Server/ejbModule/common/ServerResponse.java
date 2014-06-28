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

	/**
	 * Getter for title.
	 * 
	 * @return The title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Getter for content
	 * 
	 * @return The content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Setter for title
	 * 
	 * @param title
	 *            The title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Setter for content
	 * 
	 * @param content
	 *            The content
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
