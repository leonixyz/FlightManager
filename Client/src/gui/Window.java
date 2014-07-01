package gui;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jms.MessagingService;
import common.ClientRequest;
import common.ServerResponse;

/**
 * The window of the client.
 * 
 * @author user
 * 
 */
public class Window extends JFrame {

	private MainPanel mainPanel;
	private MessagingService messagingService;

	/**
	 * Create a client window and starts all necessary services
	 * 
	 * @return Window
	 */
	public Window() {
		super();
		this.messagingService = new MessagingService(this);
		this.mainPanel = new MainPanel(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(888, 666));
		this.setLocation(111, 55);
		this.getContentPane().add(mainPanel);
		this.setLayout(new CardLayout());
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Dispatches the request to the JMS
	 * 
	 * @param request
	 *            The request to be forwarded
	 */
	public void dispatchRequest(ClientRequest request) {
		this.messagingService.sendRequest(request);
	}

	/**
	 * Dispatches the response to the JMS
	 * 
	 * @param response
	 *            The response to be forwarded
	 */
	public void dispatchResponse(ServerResponse response) {
		this.mainPanel.addQueryTab(response);
	}

	/**
	 * Pops up an error message
	 * 
	 * @param response
	 *            The response to be forwarded
	 */
	public static void sendError(String error) {
		JOptionPane.showMessageDialog(null, error);
	}
}
