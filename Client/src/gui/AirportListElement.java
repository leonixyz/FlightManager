package gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AirportListElement {
	
	// hash map containing pairs "airport-name" / "IATA-code"
	public static final Map<String, String> AIRPORTS;
	// initialization of the hash map
	static{
		AIRPORTS = new HashMap<String, String>();
		// load the xml and parse it
		File xml = new File("resources/airport-codes.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xml);
		} catch (ParserConfigurationException e) {
			Window.sendError("Warning: cannot parse XML data for loading IATA airport codes,\nthe autocompletion might not work.");
		} catch (SAXException e) {
			Window.sendError("Warning: cannot parse XML data for loading IATA airport codes,\nthe autocompletion might not work.");
		} catch (IOException e) {
			Window.sendError("Warning: cannot parse XML data for loading IATA airport codes,\nthe autocompletion might not work.");
		}
		
		if(doc!=null){
			// populating the hash map with key/values pairs
			doc.getDocumentElement().normalize();
			NodeList airports = doc.getElementsByTagName("iata_airport_codes");
			for (int i = 0; i < airports.getLength(); i++) {
				Node currentAirportNode = airports.item(i);
				if (currentAirportNode.getNodeType() == Node.ELEMENT_NODE) {
					Element currentAirport = (Element) currentAirportNode;
					AIRPORTS.put(currentAirport.getElementsByTagName("airport").item(0).getTextContent(), currentAirport.getElementsByTagName("code").item(0).getTextContent());
				}
			}
		}
	}

	private String name, code;
	
	public AirportListElement(String name){
		this.name = name;
		this.code = getCode(name);
	}
	
	public String getCode(String name){
		return null;
	}
	
	public String getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
}
