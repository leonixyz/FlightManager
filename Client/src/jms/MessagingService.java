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

/**
 * This class is responsible for the JMS part of the client.
 * 
 * @author user
 * 
 */
public class MessagingService {

	private static final String JBOSS_HOST = "localhost";
	private static final int JBOSS_PORT = 5455;

	private static Map connectionParams = new HashMap();

	private Window window;
	private ConnectionFactory connectionFactory;
	private Queue remoteQueue;

	/**
	 * Initializes a class responsible for managing the JMS part of the client.
	 * 
	 * @param myWindow
	 *            The client window that want this service
	 */
	public MessagingService(Window myWindow) {
		this.window = myWindow;
		MessagingService.connectionParams.put(
				TransportConstants.PORT_PROP_NAME, JBOSS_PORT);
		MessagingService.connectionParams.put(
				TransportConstants.HOST_PROP_NAME, JBOSS_HOST);
		TransportConfiguration transportConfiguration = new TransportConfiguration(
				NettyConnectorFactory.class.getName(), connectionParams);
		this.connectionFactory = (ConnectionFactory) HornetQJMSClient
				.createConnectionFactoryWithoutHA(JMSFactoryType.CF,
						transportConfiguration);
		this.remoteQueue = HornetQJMSClient.createQueue("testQueue");
	}

	/**
	 * Sends the message to the queue
	 * 
	 * @param clientRequest
	 *            The request of the client that needs to be forwarded
	 * @return 
	 * 
	 * @return null
	 */
	public boolean sendRequest(ClientRequest clientRequest) {

		// create a connection
		QueueConnection connection = null;
		try {
			connection = (QueueConnection) this.connectionFactory
					.createConnection();
		} catch (JMSException e) {
			Window.sendError("Couldn't initialize the connection to the server.");
			return false;
		}

		// creates a session
		QueueSession session;
		try {
			session = connection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			Window.sendError("Couldn't get a session from the connection to the server.\nAre you using the right JRE?");
			return false;
		}

		// create a temporary queue to get the response back
		QueueRequestor requestor;
		try {
			requestor = new QueueRequestor(session, this.remoteQueue);
		} catch (JMSException e) {
			Window.sendError("Cannot create a temporary JMS queue to get the response back.");
			return false;

		}

		// connect to the server
		try {
			connection.start();
		} catch (JMSException e) {
			Window.sendError("The JMS is doing nasty things.\nCannot start the connection.\nThe request has been ignored.");
			return false;
		}

		// create a message for the request
		ObjectMessage requestMessage;
		try {
			requestMessage = session.createObjectMessage();
		} catch (JMSException e) {
			Window.sendError("The JMS is doing nasty things.\nCannot create an object message.\nThe request has been ignored.");
			return false;
		}

		// wrap the request into the message
		try {
			requestMessage.setObject(clientRequest);
		} catch (JMSException e) {
			Window.sendError("The JMS is doing nasty things.\nCannot set the object message.\nThe request has been ignored.");
			return false;
		}

		// wait for the response on the temporary queue
		Message responseMessage;
		try {
			responseMessage = requestor.request(requestMessage);
		} catch (JMSException e) {
			Window.sendError("The JMS is doing nasty things.\nCannot get the response back.\nYour request has been lost.");
			return false;
		}

		// process the response
		ServerResponse serverResponse = null;
		if (responseMessage instanceof ObjectMessage) {
			try {
				serverResponse = (ServerResponse) ((ObjectMessage) responseMessage)
						.getObject();
			} catch (JMSException e) {
				Window.sendError("The JMS is doing nasty things.\nCannot get a valid response back.\nThe response at your request has been lost.");
				return false;
			}
			this.window.dispatchResponse(serverResponse);
		} else {
			Window.sendError("The reponse from the server is malformed, either this client is\nnot compatible with the server, or you\nare under some kind of attack.");
			return false;
		}

		// close resources
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				Window.sendError("Cannot close the connection.");
				return false;
			}
		}
		
		return true;
	}
}
