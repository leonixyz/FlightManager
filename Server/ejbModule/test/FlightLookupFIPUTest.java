package test;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import common.ServerResponse;

import fipu.FlightLookupFIPU;

public class FlightLookupFIPUTest {

	private static final String RESULT =
			  "[+ Total Trip Time: PT1H40M\tTotal Miles: 515\tFlight Legs: 1 ]\n"
			+ " +--| #1\n"
			+ "    | FROM Milan Malpensa (MXP) TO Berlin Schoenefeld (SXF) ON Easyjet\n"
			+ "    | Flight #:\t4672\n"
			+ "    | Start:\t\t2014-08-15T11:25:00 +0200\n"
			+ "    | End #:\t\t2014-08-15T13:05:00 +0200\n"
			+ "    | Duration:\tPT1H40M\n"
			+ "    | Distance:\t515\n"
			+ "\n"
			+ "------------------------\n"
			+ "[+ Total Trip Time: PT1H40M\tTotal Miles: 515\tFlight Legs: 1 ]\n"
			+ " +--| #1\n"
			+ "    | FROM Milan Malpensa (MXP) TO Berlin Schoenefeld (SXF) ON Easyjet\n"
			+ "    | Flight #:\t4676\n"
			+ "    | Start:\t\t2014-08-15T21:15:00 +0200\n"
			+ "    | End #:\t\t2014-08-15T22:55:00 +0200\n"
			+ "    | Duration:\tPT1H40M\n" 
			+ "    | Distance:\t515\n"
			+ "\n"
			+ "------------------------\n";

	@Test
	public void testRequestResponse() throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2014-08-15");
		ServerResponse response = new FlightLookupFIPU().requestResponse("MXP", "SXF", date);
		assertTrue(RESULT.equals(response.getContent()));

	}

}
