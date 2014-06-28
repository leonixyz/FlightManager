package fipu;

import java.util.Date;

import common.ServerResponse;

/**
 * This is the flight information providing unit (FIPU) for the specific API of
 * FlightStats.com
 * 
 * @author user
 * 
 */
public class FlightStatsFIPU extends FIPU {

	/**
	 * Sends a request to flightstats.com and returns a response to the caller.
	 * 
	 * @param fromCode
	 *            Airport code
	 * @param toCode
	 *            Arrival airport Code
	 * @param date
	 *            When?
	 * 
	 * @return ServerResponse
	 */
	@Override
	public ServerResponse requestResponse(String fromCode, String toCode,
			Date date) {
		// TODO Auto-generated method stub
		return null;
	}

}
