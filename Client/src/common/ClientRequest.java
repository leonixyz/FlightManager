package common;

import java.io.Serializable;
import java.util.Date;

/**
 * The request that the user sends to the server containing all the data needed
 * to perform an API call to the providers of flight information.
 * 
 * @author user
 * 
 */
public class ClientRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	// defines which webservice to use
	public static final int FLIGHT_LOOKUP = 0, FLIGHT_STATS = 1;

	// instance data
	private String departure, arrival;
	private Date date;
	private int desiredWebservice;

	/**
	 * Creates the ClientRequest
	 * 
	 * @param departure
	 *            Where does the plane leave from
	 * @param arrival
	 *            Where does the plane fly to
	 * @param date
	 *            When
	 * @param desiredWebservice
	 *            Which service to use as API
	 * 
	 * @return ClientRequest
	 */
	public ClientRequest(String departure, String arrival, Date date,
			int desiredWebservice) {
		this.setDeparture(departure);
		this.setArrival(arrival);
		this.setDate(date);
		this.setDesiredWebservice(desiredWebservice);
	}

	/**
	 * Getter for the date
	 * 
	 * @return The date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Setter for the date
	 * 
	 * @param date
	 *            The date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Getter for the departure IATA code
	 * 
	 * @return Departure IATA code
	 */
	public String getDeparture() {
		return this.departure;
	}

	/**
	 * Setter for the departure IATA code
	 * 
	 * @param departure
	 *            Departure IATA code
	 */
	public void setDeparture(String departure) {
		this.departure = departure;
	}

	/**
	 * Getter for the arrival IATA code
	 * 
	 * @return Arrival IATA code
	 */
	public String getArrival() {
		return this.arrival;
	}

	/**
	 * Setter for the arrival IATA code
	 * 
	 * @param arrival
	 *            Arrival IATA code
	 */
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	/**
	 * Getter for the web service to use
	 * 
	 * @return Number identifying the desired web service
	 */
	public int getDesiredWebservice() {
		return this.desiredWebservice;
	}

	/**
	 * Setter for the web service to use
	 * 
	 * @param desiredWebservice
	 *            Number identifying the desired web service
	 */
	public void setDesiredWebservice(int desiredWebservice) {
		this.desiredWebservice = desiredWebservice;
	}

	/**
	 * Translate the object to a string representation.
	 * 
	 * @return The string representation
	 */
	@Override
	public String toString() {
		return "ClientRequest [departure=" + this.departure + ", arrival="
				+ this.arrival + ", date=" + this.date + ", desiredWebservice="
				+ this.desiredWebservice + "]";
	}
}
