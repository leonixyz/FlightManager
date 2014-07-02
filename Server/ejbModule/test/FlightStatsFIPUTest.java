package test;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import common.ServerResponse;

import fipu.FlightStatsFIPU;

public class FlightStatsFIPUTest {
	
	private final static String RESULT = "Flights from Milano Malpensa Airport, Milan(MXP) to Schoenefeld Airport, Berlin(SXF)\n"+
			"\n"+
			"[1] flight number 4672 on easyJet\n"+
			"  Departure: 2014-08-15T11:25:00.000\n"+
			"  Arrival:   2014-08-15T13:05:00.000\n"+
			"\n"+
			"[2] flight number 4676 on easyJet\n"+
			"  Departure: 2014-08-15T21:15:00.000\n"+
			"  Arrival:   2014-08-15T22:55:00.000\n\n";

	@Test
	public void testRequestResponse() throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2014-08-15");
		ServerResponse response = new FlightStatsFIPU().requestResponse("MXP", "SXF", date);
		assertTrue(RESULT.equals(response.getContent()));
	}

	

	
}
