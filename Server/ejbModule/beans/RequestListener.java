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


 /** 
   * Listens on incoming messages
   *
   * @param requestMessage The message incoming from the request
   * @return void
  */
	public void onMessage(Message requestMessage) {
		Connection connection = null;
		try {
			// manage the client request
			ClientRequest clientRequest = (ClientRequest) ((ObjectMessage) requestMessage).getObject();

			String departure = clientRequest.getDeparture();
			String destination = clientRequest.getArrival();
			Date date = clientRequest.getDate();
			int desiredWebservice = clientRequest.getDesiredWebservice();

			// create a connection
			connection = connectionFactory.createConnection();

			// create a JMS session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// create a producer for the reply queue
			Destination replyDestination = requestMessage.getJMSReplyTo();
			MessageProducer producer = session.createProducer(replyDestination);

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
			ObjectMessage response = session.createObjectMessage();
			response.setObject(serverResponse);
			producer.send(response);
			session.close();
			
		} catch (Exception e) {
			//TODO splittare e regolamentare
			e.printStackTrace();
			
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
					//TODO print error
				}
			}
		}
	}
}
