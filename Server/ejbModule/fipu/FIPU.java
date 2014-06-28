package fipu;

import java.util.Date;

import common.ServerResponse;

/**
 * This is the flight information providing unit (FIPU) for a generic API
 * provider.
 * 
 * @author user
 * 
 */
public abstract class FIPU {

	/**
	 * Sends a request to a generic API provider and returns a response to the
	 * caller.
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
	public abstract ServerResponse requestResponse(String fromCode,
			String toCode, Date date);
}
