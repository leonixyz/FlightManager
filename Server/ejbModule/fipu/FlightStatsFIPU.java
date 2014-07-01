package fipu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import common.ServerResponse;

/**
 * This is the flight information providing unit (FIPU) for the specific API of
 * FlightStats.com
 * 
 * @author user
 * 
 */
public class FlightStatsFIPU extends FIPU {
	
	private static final String APP_ID	= "b02cde6d";
	private static final String APP_KEY = "0f2196592fd8eaf74f5a280a4b54c22e";

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
		URL requestUrl = null;
		// manage date to end up with three values MM DD YY
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
		String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
		String year = String.format("%02d", cal.get(Calendar.YEAR) - 2000);

		//		RESPONSE EXAMPLE
		//////////////////////////////////////////////
		//		    {
		//			   "scheduledFlights":[
		//			      {
		//			         "carrierFsCode":"BA",
		//			         "flightNumber":"953",
		//			         "departureTime":"2014-08-10T16:25:00.000",
		//			         "arrivalTime":"2014-08-10T17:20:00.000",
		//			      },
		//			      . . .
		//			      . . .
		//			      . . .
		//			   },
		//			   "appendix":{
		//			      "airlines":[
		//			         {
		//			            "fs":"CX",
		//			            "name":"Cathay Pacific",
		//			         },
		//			         . . .
		//			         . . .
		//			         . . .
		//			      ],
		//			      "airports":[
		//			         {
		//			            "iata":"MUC",
		//			            "name":"Franz Josef Strauss Airport",
		//			            "city":"Munich",
		//			         },
		//			         {
		//			            "iata":"LHR",
		//			            "name":"London Heathrow Airport",
		//			            "city":"London",
		//			         }
		//			      ],
		//			   }
		//			}
		//////////////////////////////////////////////
		
		InputStream is = null;
		try {
			requestUrl = new URL("https://api.flightstats.com/flex/schedules/rest/v1/json/from/"+fromCode+"/to/"+toCode+"/departing/"+year+"}/"+month+"/"+day+"?appId="+APP_ID+"&appKey="+APP_KEY);
		
			is = requestUrl.openStream();
		
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}		
			JSONObject json = new JSONObject(sb.toString());
		} catch(MalformedURLException e) {
		
	
		} catch (IOException e) {
			// cannot openStream
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// cannot close inputstream
			}
		}
		
		return null;
	}

}
