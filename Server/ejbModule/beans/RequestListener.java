package beans;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import common.ClientRequest;
import common.ServerResponse;
import fipu.FIPU;
import fipu.FlightLookupFIPU;
import fipu.FlightStatsFIPU;

@MessageDriven(name = "RequestListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/testQueue") })

public class RequestListener implements MessageListener {
	@Resource(mappedName = "java:/JmsXA")
	ConnectionFactory connectionFactory;
	
	public static void logError(String error){
		System.err.println(error);
	}

	 /** 
	   * Listener for incoming messages that is also responsible to build and send the response back.
	   *
	   * @param requestMessage The message incoming from the request
	   * @return void
	  */
	public void onMessage(Message requestMessage) {
		
		Connection connection = null;
		ClientRequest clientRequest = null;
		
		// manage the client request
		try {
			clientRequest = (ClientRequest) ((ObjectMessage) requestMessage).getObject();
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to set the object due to some internal error. ");
			return;
		}

		String departure = clientRequest.getDeparture();
		String destination = clientRequest.getArrival();
		Date date = clientRequest.getDate();
		int desiredWebservice = clientRequest.getDesiredWebservice();

		// create a connection
		try {
			connection = connectionFactory.createConnection();
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to create the connection due to some internal error.");
			return;
		}

		// create a JMS session
		Session session;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e1) {
			logError("[RequestListener] The Connection object failed to create a session due to some internal error or lack of support for the specific transaction and acknowledgement mode.");
			return;
		}

		// extract the replyDestination property
		Destination replyDestination;
		try {
			replyDestination = requestMessage.getJMSReplyTo();
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to get the JMSReplyTo destination due to some internal error.");
			return;
		}
		
		// create a message producer for the reply
		MessageProducer producer;
		try {
			producer = session.createProducer(replyDestination);
		} catch (JMSException e1) {
			logError("[RequestListener] The session failed to create a MessageProducer due to some internal error.");
			return;
		}

		// choose the webservice to use
		FIPU desiredFIPU = null;
		switch(desiredWebservice) {
		case ClientRequest.FLIGHT_LOOKUP:
			desiredFIPU = new FlightLookupFIPU(); 
			break;
		case ClientRequest.FLIGHT_STATS:
			desiredFIPU = new FlightStatsFIPU();
		}

		// build the response and send it back
		ServerResponse serverResponse = desiredFIPU.requestResponse(departure, destination, date);
		ObjectMessage response;
		try {
			response = session.createObjectMessage();
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to create the reply message due to some internal error.");
			return;
		}
		
		try {
			response.setObject(serverResponse);
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to set the object due to some internal error. ");
			return;
		}
		
		try {
			producer.send(response);
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to send the message due to some internal error. ");
			return;
		}
		
		// close resources
		try {
			session.close();
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to close the session due to some internal error. ");
			return;
		}

		try {
			connection.close();
		} catch (JMSException e) {
			logError("[RequestListener] The JMS provider failed to close the connection due to some internal error. ");
			return;
		}
	}
}