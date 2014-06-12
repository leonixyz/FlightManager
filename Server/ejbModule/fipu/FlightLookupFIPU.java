package fipu;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.ServerResponse;

public class FlightLookupFIPU extends FIPU {

	private static final String DEVELOPER_KEY = "4383495C-4B5E-4229-8446-E8C203783296";
	
	public ServerResponse requestResponse(String fromCode, String toCode, Date date){
		StringBuilder s = new StringBuilder();
		try{
			
			// manage date to end up with three values MM DD YY
		    Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        String day 	 = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
	        String month = String.format("%02d", cal.get(Calendar.MONTH)+1);
	        String year  = String.format("%02d", cal.get(Calendar.YEAR)-2000);

			///////////////// DEFINITIVE WAY ////////////////
//			URL url = new URL("http://api.flightlookup.com/otajtimetable/v1/TimeTable/?From="+fromCode+"&To="+toCode+"&Date="+month+"%2F"+day+"%2F"+year+"&key="+DEVELOPER_KEY);
//			URLConnection connection = url.openConnection();
//			InputStream stream = connection.getInputStream();
//	        DocumentBuilderFactory objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
//	        DocumentBuilder objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
//	        Document doc = objDocumentBuilder.parse(stream);
			///////////////// DEFINITIVE WAY ////////////////


			///////////////// TEMPORARY WAY ////////////////
			File fXmlFile = new File("/Users/user/api.flightlookup.com.xml");	//TODO change and use the proper file path
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			///////////////// TEMPORARY WAY ////////////////


			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList flights = doc.getElementsByTagName("FlightDetails");

			if(doc.getDocumentElement().getElementsByTagName("Success").item(0)==null){
				// the request was unsuccessful if no <Success></Success> XML tag is present
				//TODO throw an exception and manage it
				return null;
			}

			// parse the document
			for (int i = 0; i < flights.getLength(); i++) {

				// foreach node consisting in the flight solution...
				Node currentFlightNode = flights.item(i);

				if (currentFlightNode.getNodeType() == Node.ELEMENT_NODE) {

					Element currentFlightElement = (Element) currentFlightNode;

					s.append("[+ Total Trip Time: " + currentFlightElement.getAttribute("TotalTripTime")+"\t");
					s.append("Total Miles: " + currentFlightElement.getAttribute("TotalMiles")+"\t");
					s.append("Flight Legs: " + currentFlightElement.getAttribute("FLSFlightLegs"));
					s.append(" ]\n");

					NodeList legs = currentFlightElement.getElementsByTagName("FlightLegDetails");

					// foreach single journey that is part of the flight solution...
					for(int j=0; j<legs.getLength(); j++){
						Node currentLegNode = legs.item(j);
						if (currentLegNode.getNodeType() == Node.ELEMENT_NODE) {
							Element currentLegElement = (Element) currentLegNode;
							String departureAirportName =((Element) currentLegElement.getElementsByTagName("DepartureAirport").item(0)).getAttribute("FLSLocationName");
							String departureAirportCode =((Element) currentLegElement.getElementsByTagName("DepartureAirport").item(0)).getAttribute("LocationCode");
							String arrivalAirportName =((Element) currentLegElement.getElementsByTagName("ArrivalAirport").item(0)).getAttribute("FLSLocationName");
							String arrivalAirportCode =((Element) currentLegElement.getElementsByTagName("ArrivalAirport").item(0)).getAttribute("LocationCode");
							String airline =((Element) currentLegElement.getElementsByTagName("MarketingAirline").item(0)).getAttribute("CompanyShortName");

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

		} catch (Exception e) {
			//TODO split and handle properly
			e.printStackTrace();
		}
		return new ServerResponse("FlightLookup.com", s.toString());
	}

}
