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

/**
 * This is the Java Bean responsible to listen on a JMS queue for client
 * incoming requests and dispatch them to the correct flight information
 * providing unit (FIPU), and once the response is back it is sent back to the
 * right client.
 * 
 * @author user
 * 
 */
@MessageDriven(name = "RequestListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/testQueue") })
public class RequestListener implements MessageListener {
	@Resource(mappedName = "java:/JmsXA")
	private static ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;

	public static void logError(String error) {
		System.err.println(error);
	}

	/**
	 * Listener for incoming messages that is also responsible to build and send
	 * the response back.
	 * 
	 * @param requestMessage
	 *            The message incoming from the request
	 */
	public void onMessage(Message requestMessage) {

		// get a valid request out of the message
		ClientRequest clientRequest = this.initializeRequest(requestMessage);
		if (clientRequest == null) {
			// error message already logged
			return;
		}

		// extract useful information
		String departure = clientRequest.getDeparture();
		String destination = clientRequest.getArrival();
		Date date = clientRequest.getDate();
		int desiredWebservice = clientRequest.getDesiredWebservice();

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
		switch (desiredWebservice) {
		case ClientRequest.FLIGHT_LOOKUP:
			desiredFIPU = new FlightLookupFIPU();
			break;
		case ClientRequest.FLIGHT_STATS:
			desiredFIPU = new FlightStatsFIPU();
		}

		this.sendReply(producer, departure, destination, date, desiredFIPU);

	}

	/**
	 * Sends a valid response back to the producer of the request.
	 * 
	 * @param producer
	 *            The producer of the request
	 * @param departure
	 *            Departure IATA airport code
	 * @param destination
	 *            Destination IAIA airport code
	 * @param date
	 *            Departure date
	 * @param desiredFIPU
	 *            FIPU chosen for processing the request
	 */
	private void sendReply(MessageProducer producer, String departure,
			String destination, Date date, FIPU desiredFIPU) {
		// build the response and send it back
		ServerResponse serverResponse = desiredFIPU.requestResponse(departure,
				destination, date);
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

	/**
	 * Transforms the object message sent by the client into a valid request
	 * 
	 * @param requestMessage
	 *            The object message wrapping the client request
	 * @return The request of the client
	 */
	private ClientRequest initializeRequest(Message requestMessage) {

		ClientRequest clientRequest = null;
		// manage the client request
		try {
			clientRequest = (ClientRequest) ((ObjectMessage) requestMessage)
					.getObject();
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to get the object out of the message due to some internal error. ");
			return null;
		}

		// create a connection
		try {
			connection = connectionFactory.createConnection();
		} catch (JMSException e1) {
			logError("[RequestListener] The JMS provider failed to create the connection due to some internal error.");
			return null;
		}

		// create a JMS session
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e1) {
			logError("[RequestListener] The Connection object failed to create a session due to some internal error or lack of support for the specific transaction and acknowledgement mode.");
			return null;
		}

		return clientRequest;
	}
}