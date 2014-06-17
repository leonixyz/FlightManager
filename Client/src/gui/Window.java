package gui;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import jms.MessagingService;

import common.ClientRequest;
import common.ServerResponse;

public class Window extends JFrame{
	
	private MainPanel mainPanel;
	private MessagingService messagingService;
	
	public Window(){
		super();
		this.messagingService = new MessagingService(this);
		this.mainPanel = new MainPanel(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(888,666));
		this.setLocation(111,55);
		this.getContentPane().add(mainPanel);
		this.setLayout(new CardLayout());
		this.pack();
		this.setVisible(true);		
	}
	
	public void dispatchRequest(ClientRequest request){
		//TODO controllare se si puo mandare la richiesta
		this.messagingService.sendRequest(request);
	}
	
	public void dispatchResponse(ServerResponse response){
		//TODO controllare se si puo fare
		this.mainPanel.addQueryTab(response);
	}
}
