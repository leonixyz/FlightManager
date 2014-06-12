package jms;

import gui.Window;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;

import common.ClientRequest;
import common.ServerResponse;

public class MessagingService{

	private static final String JBOSS_HOST = "localhost";
	private static final int JBOSS_PORT = 5455;
	
	private static Map connectionParams = new HashMap();
	
	private Window window;
	private ConnectionFactory connectionFactory;
	private Queue remoteQueue;
	
	
	public MessagingService(Window myWindow){
		this.window = myWindow;
	    MessagingService.connectionParams.put(TransportConstants.PORT_PROP_NAME, JBOSS_PORT);
	    MessagingService.connectionParams.put(TransportConstants.HOST_PROP_NAME, JBOSS_HOST);
		TransportConfiguration transportConfiguration =  new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionParams);
		this.connectionFactory = (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
		this.remoteQueue = HornetQJMSClient.createQueue("testQueue");
	}
	
	
	public void sendRequest(ClientRequest clientRequest) {
		QueueConnection connection = null;
		try {
			connection = (QueueConnection) this.connectionFactory.createConnection();
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueRequestor requestor = new QueueRequestor(session, this.remoteQueue);
			connection.start();
			
			ObjectMessage requestMessage = session.createObjectMessage();
			requestMessage.setObject(clientRequest);
			
		    Message responseMessage;
		    responseMessage=requestor.request(requestMessage);
		    
		    ServerResponse serverResponse = null;
		    if (responseMessage instanceof ObjectMessage){
		        serverResponse = (ServerResponse)((ObjectMessage) responseMessage).getObject();
			    this.window.dispatchResponse(serverResponse);
		    }
		    else{
		        //TODO error
		    }
		} catch (JMSException e) {
			// TODO splittare e differenziare
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (JMSException e) {
				//TODO print error
			}
		}
	}
}