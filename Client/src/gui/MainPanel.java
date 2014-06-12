package gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import common.ClientRequest;
import common.ServerResponse;

public class MainPanel extends JPanel {
	
	private JTabbedPane tabbedPane;
	private JTextField departureTextField, arrivalTextField;
    private Window parentWindow;
	private JXDatePicker datePicker;
	private JComboBox departureComboBox, arrivalComboBox;
    
	public MainPanel(Window parentWindow){
		super();
		this.parentWindow = parentWindow;
		
		// initialize fields for user input
		this.departureTextField = new JTextField(3);
		this.departureTextField.setMaximumSize(new Dimension(80,30));
		this.arrivalTextField = new JTextField(3);
		this.arrivalTextField.setMaximumSize(new Dimension(80,30));
		this.datePicker = new JXDatePicker();
		// initialize the rest
		this.setLayout(new CardLayout());
		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.addTab("Fill request", this.getRequestPanel());
		this.add(this.tabbedPane);
	}
	
	private JPanel getRequestPanel(){
		// build the initial panel where the user input its data
		JPanel requestPanel = new JPanel();
		requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
		requestPanel.add(Box.createVerticalGlue());
		
		// section for departure airport
		JPanel departurePanel = new JPanel();
			departurePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			departurePanel.add(new JLabel("Departure airport"));
			Object[] airportList = AirportListElement.AIRPORTS.keySet().toArray();
			departureComboBox = new JComboBox(airportList);
			AutoCompleteDecorator.decorate(departureComboBox);
			departureComboBox.setSelectedItem(null);
			departurePanel.add(departureComboBox);
			departurePanel.add(Box.createRigidArea(new Dimension(200,1)));
		requestPanel.add(departurePanel);
		
		requestPanel.add(Box.createVerticalGlue());
		requestPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		requestPanel.add(Box.createVerticalGlue());

		// section for arrival airport
		JPanel arrivalPanel = new JPanel();
			arrivalPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			arrivalPanel.add(new JLabel("Arrival airport"));
			arrivalComboBox = new JComboBox(airportList);
			AutoCompleteDecorator.decorate(arrivalComboBox);
			arrivalComboBox.setSelectedItem(null);
			arrivalPanel.add(arrivalComboBox);
			arrivalPanel.add(Box.createRigidArea(new Dimension(200,1)));
		requestPanel.add(arrivalPanel);

		requestPanel.add(Box.createVerticalGlue());
		requestPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		requestPanel.add(Box.createVerticalGlue());
		
		// section for date
		JPanel datePanel = new JPanel();
			datePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			datePanel.add(new JLabel("Select departure date"));
        	datePicker.setDate(Calendar.getInstance().getTime());
        	datePicker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
        	datePanel.add(datePicker);
			datePanel.add(Box.createRigidArea(new Dimension(200,1)));
		requestPanel.add(datePanel);
		
		requestPanel.add(Box.createVerticalGlue());
		requestPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		requestPanel.add(Box.createVerticalGlue());
		
		// section for starting commands from the gui
		JPanel menuPanel = new JPanel();
			JRadioButton flightLookupOption = new JRadioButton("FlightLookup.com");
			flightLookupOption.setActionCommand("flightlookup");
			flightLookupOption.setSelected(true);
			JRadioButton flightStatsOption = new JRadioButton("FlightStats.com");
			flightStatsOption.setActionCommand("flightstats");
			ButtonGroup desiredWebservice = new ButtonGroup();
			desiredWebservice.add(flightLookupOption);
			desiredWebservice.add(flightStatsOption);
			menuPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			JButton queryButton = new JButton("Query");
			queryButton.addActionListener(new SendQueryAction());
			menuPanel.add(flightLookupOption);
			menuPanel.add(flightStatsOption);
			menuPanel.add(queryButton);
			menuPanel.add(Box.createRigidArea(new Dimension(200,1)));
		requestPanel.add(menuPanel);
		
		requestPanel.add(Box.createVerticalGlue());	//	
		requestPanel.add(Box.createVerticalGlue());	//	
		requestPanel.add(Box.createVerticalGlue());	//	h
		requestPanel.add(Box.createVerticalGlue());	//	o
		requestPanel.add(Box.createVerticalGlue());	//	r
		requestPanel.add(Box.createVerticalGlue());	//	r
		requestPanel.add(Box.createVerticalGlue());	//	i
		requestPanel.add(Box.createVerticalGlue());	//	b
		requestPanel.add(Box.createVerticalGlue());	//	l
		requestPanel.add(Box.createVerticalGlue());	//	e
		requestPanel.add(Box.createVerticalGlue());	//	
		requestPanel.add(Box.createVerticalGlue());	//	
		
		return requestPanel;
	}
	
	
	public void addQueryTab(ServerResponse response){
		// add a new tab to the main tabbed pane with given a server response
		JTextArea contentDisplayer = new JTextArea();
		contentDisplayer.setText(response.getContent());
		
		JPanel queryPanel = new JPanel();
			queryPanel.setLayout(new CardLayout());
			JScrollPane scrollPane = new JScrollPane();
				scrollPane.getViewport().add( contentDisplayer );
			queryPanel.add(scrollPane);
		this.tabbedPane.addTab(response.getTitle(), queryPanel);
		//TODO costruire un tab a partire da una struttura dati dinamica
		//     che costituisce la risposta del server alla query dell'utente
	}
	
	
	
	private class SendQueryAction implements ActionListener{
		// this action listener is triggered when the user
		// sends its query to the server
		@Override
		public void actionPerformed(ActionEvent e) {
			// selecting the webservice
			int webservice = 0;
			switch(e.getActionCommand()){
			case "flightlookup":
				webservice = ClientRequest.FLIGHT_LOOKUP;
				break;
			case "":
				webservice = ClientRequest.FLIGHT_STATS;
				break;
			}
			
			// building the request
			ClientRequest request = new ClientRequest(
					departureTextField.getText(),
					arrivalTextField.getText(),
					datePicker.getDate(),
					webservice
				);
			
			// dispatching the request
			parentWindow.dispatchRequest(request);
		}
	}
	
	
}
