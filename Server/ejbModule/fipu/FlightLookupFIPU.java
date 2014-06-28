package fipu;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import beans.RequestListener;

import common.ServerResponse;

public class FlightLookupFIPU extends FIPU {

	private static final String DEVELOPER_KEY = "4383495C-4B5E-4229-8446-E8C203783296";

 /**
  * Handles the server response
  *
  * @param fromCode Airport code
  * @param toCode Arrival airport Code
  * @param date When?
  *
  * @return ServerResponse
  */  
	public ServerResponse requestResponse(String fromCode, String toCode, Date date){
		StringBuilder s = new StringBuilder();
		// manage date to end up with three values MM DD YY
	    Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day 	 = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
        String month = String.format("%02d", cal.get(Calendar.MONTH)+1);
        String year  = String.format("%02d", cal.get(Calendar.YEAR)-2000);

		URL url = null;
		Document doc;
		
        // send the request and parse it
		try {
			url = new URL("http://api.flightlookup.com/otajtimetable/v1/TimeTable/?From="+fromCode+"&To="+toCode+"&Date="+month+"%2F"+day+"%2F"+year+"&key="+DEVELOPER_KEY);
			URLConnection connection = url.openConnection();
			InputStream stream = connection.getInputStream();
	        DocumentBuilderFactory objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
	        doc = objDocumentBuilder.parse(stream);
		} catch (MalformedURLException e) {
			RequestListener.logError("[FlightLookupFIPU] There is an error in the URL for sending the request to FlightLookup.com");
			return null;
		} catch (IOException e) {
			RequestListener.logError("[FlightLookupFIPU] Couldn open a connection to the url "+url.toString());
			return null;
		} catch (ParserConfigurationException e) {
			RequestListener.logError("[FlightLookupFIPU] DocumentBuilder cannot be created satisfying the configuration requested.");
			return null;
		} catch (SAXException e) {
			RequestListener.logError("[FlightLookupFIPU] A parse error occurred while processing the response.");
			return null;
		}

		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		NodeList flights = doc.getElementsByTagName("FlightDetails");

		if(doc.getDocumentElement().getElementsByTagName("Success").item(0)==null){
			// the request was unsuccessful if no <Success></Success> XML tag is present
			RequestListener.logError("[FlightLookupFIPU] FlightLookup.com returned an unsuccessful response.");
			return null;
		}

		// parse the document to produce only useful data
		for (int i = 0; i < flights.getLength(); i++) {

			// for each node consisting in the flight solution...
			Node currentFlightNode = flights.item(i);

			if (currentFlightNode.getNodeType() == Node.ELEMENT_NODE) {

				Element currentFlightElement = (Element) currentFlightNode;

				s.append("[+ Total Trip Time: " + currentFlightElement.getAttribute("TotalTripTime")+"\t");
				s.append("Total Miles: " + currentFlightElement.getAttribute("TotalMiles")+"\t");
				s.append("Flight Legs: " + currentFlightElement.getAttribute("FLSFlightLegs"));
				s.append(" ]\n");

				NodeList legs = currentFlightElement.getElementsByTagName("FlightLegDetails");

				// for each single journey that is part of the flight solution...
				for(int j=0; j<legs.getLength(); j++){
					Node currentLegNode = legs.item(j);
					if (currentLegNode.getNodeType() == Node.ELEMENT_NODE) {
						
						// ...get all useful information...
						Element currentLegElement = (Element) currentLegNode;
						String departureAirportName =((Element) currentLegElement.getElementsByTagName("DepartureAirport").item(0)).getAttribute("FLSLocationName");
						String departureAirportCode =((Element) currentLegElement.getElementsByTagName("DepartureAirport").item(0)).getAttribute("LocationCode");
						String arrivalAirportName =((Element) currentLegElement.getElementsByTagName("ArrivalAirport").item(0)).getAttribute("FLSLocationName");
						String arrivalAirportCode =((Element) currentLegElement.getElementsByTagName("ArrivalAirport").item(0)).getAttribute("LocationCode");
						String airline =((Element) currentLegElement.getElementsByTagName("MarketingAirline").item(0)).getAttribute("CompanyShortName");

						// ... and produce a very primitive output
						s.append(" +--| #"+currentLegElement.getAttribute("SequenceNumber")+"\n"); 
						s.append("    | FROM "+departureAirportName+" ("+departureAirportCode+") TO "+arrivalAirportName+" ("+arrivalAirportCode+") ON "+airline+"\n");
						s.append("    | Flight #:\t"+currentLegElement.getAttribute("FlightNumber")+"\n");
						s.append("    | Start:\t\t"+currentLegElement.getAttribute("DepartureDateTime"));
						s.append(" "+currentLegElement.getAttribute("FLSDepartureTimeOffset")+"\n");
						s.append("    | End #:\t\t"+currentLegElement.getAttribute("ArrivalDateTime"));
						s.append(" "+currentLegElement.getAttribute("FLSArrivalTimeOffset")+"\n");
						s.append("    | Duration:\t"+currentLegElement.getAttribute("JourneyDuration")+"\n");
						s.append("    | Distance:\t"+currentLegElement.getAttribute("LegDistance")+"\n");
					}
					s.append("\n");
				}

				s.append("------------------------\n");
			}
		}

		return new ServerResponse("FlightLookup.com", s.toString());
	}

}
