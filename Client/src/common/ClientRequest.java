package common;

import java.io.Serializable;
import java.util.Date;

public class ClientRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// defines which webservice to use
	public static final int
		FLIGHT_LOOKUP = 0,
		FLIGHT_STATS = 1;
		
	// instance data
	private String departure, arrival;
	private Date date;
	private int desiredWebservice;
	
	public ClientRequest (String departure, String arrival, Date date, int desiredWebservice) {
		this.setDeparture(departure);
		this.setArrival(arrival);
		this.setDate(date);
		this.setDesiredWebservice(desiredWebservice);
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public String getDeparture() {
		return this.departure;
	}
	
	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return this.arrival;
	}
	
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public int getDesiredWebservice() {
		return this.desiredWebservice;
	}
	
	public void setDesiredWebservice(int desiredWebservice) {
		this.desiredWebservice = desiredWebservice;
	}

	@Override
	public String toString() {
		return "ClientRequest [departure="+this.departure+", arrival="+this.arrival+", date="+this.date+", desiredWebservice="+this.desiredWebservice+"]";
	}
}