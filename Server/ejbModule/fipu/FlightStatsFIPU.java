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
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import beans.RequestListener;
import common.ServerResponse;

/**
 * This is the flight information providing unit (FIPU) for the specific API of
 * FlightStats.com
 * 
 * @author user
 * 
 */
public class FlightStatsFIPU extends FIPU {

	private static final String APP_ID = "b02cde6d";
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
		// manage date to end up with three values MM DD YY
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
		String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
		String year = String.format("%02d", cal.get(Calendar.YEAR));

		// get the response
		JSONObject jsonResponse = getJSONResponse(fromCode, toCode, year,
				month, day);

		// extract useful fields
		JSONArray flights, airlinesTmp, airports;
		String departureAirport, arrivalAirport;
		try {
			flights = jsonResponse.getJSONArray("scheduledFlights");
			airlinesTmp = jsonResponse.getJSONObject("appendix").getJSONArray(
					"airlines");
			airports = jsonResponse.getJSONObject("appendix").getJSONArray(
					"airports");
			departureAirport = airports.getJSONObject(0).getString("name")
					+ ", " + airports.getJSONObject(0).getString("city") + "("
					+ airports.getJSONObject(0).getString("iata") + ")";
			arrivalAirport = airports.getJSONObject(1).getString("name") + ", "
					+ airports.getJSONObject(1).getString("city") + "("
					+ airports.getJSONObject(1).getString("iata") + ")";
		} catch (JSONException e) {
			JSONObject errorObject = jsonResponse.getJSONObject("error");
			String error = errorObject.getString("errorMessage");
			return new ServerResponse("Error", error);
		}

		// hashmap with <airline-code, name> values
		Map<String, String> airlines = new HashMap<String, String>();
		for (int i = 0; i < airlinesTmp.length(); i++) {
			JSONObject airline = airlinesTmp.getJSONObject(i);
			airlines.put(airline.getString("fs"), airline.getString("name"));
		}

		// processing the results to build a text response
		StringBuilder returnValue = new StringBuilder();
		returnValue.append("Flights from " + departureAirport + " to "
				+ arrivalAirport + "\n\n");
		for (int i = 0; i < flights.length(); i++) {
			JSONObject flight = flights.getJSONObject(i);
			String airline = airlines.get(flight.get("carrierFsCode"));
			String departureTime = flight.getString("departureTime");
			String arrivalTime = flight.getString("arrivalTime");
			String flightNo = flight.getString("flightNumber");
			returnValue.append("[" + (i + 1) + "] flight number " + flightNo
					+ " on " + airline + "\n");
			returnValue.append("  Departure: " + departureTime + "\n");
			returnValue.append("  Arrival:   " + arrivalTime + "\n\n");
		}

		return new ServerResponse("FlightStats.com", returnValue.toString());
	}

	/**
	 * Sends the request to FlightStats.com and returns the response of the
	 * restful API service.
	 * 
	 * @param fromCode
	 *            Departure IATA airport code
	 * @param toCode
	 *            Arrival IATA airport code
	 * @param year
	 *            Part of date of departure
	 * @param month
	 *            Part of date of departure
	 * @param day
	 *            Part of date of departure
	 * @return
	 */
	private JSONObject getJSONResponse(String fromCode, String toCode,
			String year, String month, String day) {
		URL requestUrl = null;
		InputStream inputStream = null;
		JSONObject jsonObject = null;

		// do the request!
		try {
			requestUrl = new URL(
					"https://api.flightstats.com/flex/schedules/rest/v1/json/from/"
							+ fromCode + "/to/" + toCode + "/departing/" + year
							+ "/" + month + "/" + day + "?appId=" + APP_ID
							+ "&appKey=" + APP_KEY);
			inputStream = requestUrl.openConnection().getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream, Charset.forName("UTF-8")));
			StringBuilder stringBuilder = new StringBuilder();
			int cp;
			// read one byte at a time the response
			while ((cp = bufferedReader.read()) != -1) {
				stringBuilder.append((char) cp);
			}
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (MalformedURLException e) {
			RequestListener
					.logError("[FlightStatsFIPU] There is an error in the URL for sending the request to FlightLookup.com");
		} catch (IOException e) {
			RequestListener
					.logError("[FlightStatsFIPU] Cannot open a stream for sending the request to FlightLookup.com");
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				RequestListener
						.logError("[FlightStatsFIPU] Cannot close input stream");
			}
		}
		return jsonObject;
	}

}
