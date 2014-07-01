package gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import common.ClientRequest;
import common.ServerResponse;

/**
 * This is the main panel for the client, it contains a tabbed pane with one tab
 * for getting the data from the user, and it dynamically adds a new tab for
 * each user submitted request.
 * 
 * @author user
 * 
 */
public class MainPanel extends JPanel {

	private JTabbedPane tabbedPane;
	private Window parentWindow;
	private JXDatePicker datePicker;
	private JComboBox departureComboBox, arrivalComboBox;
	private int selectedWebService = 0;

	/**
	 * Builds the main panel of the client window
	 * 
	 * @param parentWindow
	 *            The window in which the panel should be placed
	 */
	public MainPanel(Window parentWindow) {
		super();
		this.parentWindow = parentWindow;

		// initialize fields for user input
		this.datePicker = new JXDatePicker();
		// initialize the rest
		this.setLayout(new CardLayout());
		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.addTab("Fill request", this.getRequestPanel());
		this.add(this.tabbedPane);
	}

	/**
	 * Returns only a panel in which the user inserts its data for the request
	 * 
	 * @return The panel in which the user inserts its data for the request
	 */
	private JPanel getRequestPanel() {
		// build the initial panel where the user input its data
		JPanel requestPanel = new JPanel();
		requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
		requestPanel.add(Box.createVerticalGlue());

		// section for departure airport
		JPanel departurePanel = new JPanel();
		departurePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		departurePanel.add(new JLabel("Departure airport"));
		Object[] airportList = AirportListElements.AIRPORTS.keySet().toArray();
		departureComboBox = new JComboBox(airportList);
		AutoCompleteDecorator.decorate(departureComboBox);
		departureComboBox.setSelectedItem(null);
		departurePanel.add(departureComboBox);
		departurePanel.add(Box.createRigidArea(new Dimension(200, 1)));
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
		arrivalPanel.add(Box.createRigidArea(new Dimension(200, 1)));
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
		datePanel.add(Box.createRigidArea(new Dimension(200, 1)));
		requestPanel.add(datePanel);

		requestPanel.add(Box.createVerticalGlue());
		requestPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		requestPanel.add(Box.createVerticalGlue());

		// section for starting commands from the gui
		JPanel menuPanel = new JPanel();
		JRadioButton flightLookupOption = new JRadioButton("FlightLookup.com");
		this.setSelectedWebService("flightlookup.com");
		flightLookupOption.setSelected(true);
		JRadioButton flightStatsOption = new JRadioButton("FlightStats.com");
		this.setSelectedWebService("flightstats.com");
		ButtonGroup desiredWebservice = new ButtonGroup();
		desiredWebservice.add(flightLookupOption);
		desiredWebservice.add(flightStatsOption);
		menuPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton queryButton = new JButton("Query");
		queryButton.addActionListener(new SendQueryAction());
		menuPanel.add(flightLookupOption);
		menuPanel.add(flightStatsOption);
		menuPanel.add(queryButton);
		menuPanel.add(Box.createRigidArea(new Dimension(200, 1)));
		requestPanel.add(menuPanel);

		requestPanel.add(Box.createVerticalGlue()); //
		requestPanel.add(Box.createVerticalGlue()); //
		requestPanel.add(Box.createVerticalGlue()); // h
		requestPanel.add(Box.createVerticalGlue()); // o
		requestPanel.add(Box.createVerticalGlue()); // r
		requestPanel.add(Box.createVerticalGlue()); // r
		requestPanel.add(Box.createVerticalGlue()); // i
		requestPanel.add(Box.createVerticalGlue()); // b
		requestPanel.add(Box.createVerticalGlue()); // l
		requestPanel.add(Box.createVerticalGlue()); // e
		requestPanel.add(Box.createVerticalGlue()); //
		requestPanel.add(Box.createVerticalGlue()); //

		return requestPanel;
	}

	/**
	 * When the user submits a request and the response had been received, a new
	 * panel with all useful information is generated and added to the tabbed
	 * pane.
	 * 
	 * @param response
	 *            The server response containing the data to visualize
	 */
	public void addQueryTab(ServerResponse response) {

		if (response == null) {
			// consider the case that no response is given
			// i.e. no connections are available
			JOptionPane.showMessageDialog(null,
					"Sorry, there are no results satisfying these conditions.");
			return;
		}

		// add a new tab to the main tabbed pane with given a server response
		JTextArea contentDisplayer = new JTextArea();
		contentDisplayer.setFont(new Font("Monospaced", Font.PLAIN, 14));
		contentDisplayer.setText(response.getContent());
		contentDisplayer.setEditable(false);

		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new CardLayout());
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(contentDisplayer);
		queryPanel.add(scrollPane);
		this.tabbedPane.addTab(response.getTitle(), queryPanel);
		this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() - 1);
		contentDisplayer.setCaretPosition(0);
	}

	/**
	 * Setter for selectedWebService
	 * 
	 * @param webservice
	 *            Name of the web service to use. The only supported values are
	 *            actually "flightlookup.com" and "flightstats.com"
	 */
	private void setSelectedWebService(String webservice) {
		switch (webservice) {
		case "flightlookup.com":
			this.selectedWebService = ClientRequest.FLIGHT_LOOKUP;
			break;
		case "flightstats.com":
			this.selectedWebService = ClientRequest.FLIGHT_STATS;
			break;
		}
	}

	/**
	 * getter for selectedWebService
	 * 
	 * @return The selected web service. 0 for flightlookup.com, 1 for
	 *         flightstats.com
	 */
	private int getSelectedWebService() {
		return this.selectedWebService;
	}

	/**
	 * An action listener to get all data from the GUI and start a new request
	 * to the server.
	 * 
	 * @author user
	 * 
	 */
	private class SendQueryAction implements ActionListener {
		// this action listener is triggered when the user
		// sends its query to the server
		@Override
		public void actionPerformed(ActionEvent e) {
			// selecting the webservice
			int webservice = getSelectedWebService();

			// building the request

			String departureCode = AirportListElements.AIRPORTS
					.get(departureComboBox.getSelectedItem());
			String arrivalCode = AirportListElements.AIRPORTS
					.get(arrivalComboBox.getSelectedItem());

			ClientRequest request = new ClientRequest(departureCode,
					arrivalCode, datePicker.getDate(), webservice);

			// dispatching the request
			parentWindow.dispatchRequest(request);
		}
	}

}
