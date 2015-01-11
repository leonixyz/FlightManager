FlightManager
=============
## Documentation
The end-user documentation for the project in into the "/doc" folder


## What is it?

Project for the course of Distributed Systems and Internet Technologies
The followings is the requirements document provided by the teaching assistant.





Project II
Implement a system that provides information about flights.
A generic end-user asks the system informations about which (and when) airplanes are flying from an airport to another airport in a specified date.

Architecture
The system is composed by an application Front-end for the end-user (CFE) and the server application (SA) which includes also some additional unit FIPU (see below).
Also an optional web service (AF) can be implemented.

######Client Front-End (CFE)

The user Front-End CFE provides a window interface in which the end-user can insert the arguments of his query (e.g.: airport from, airport to, datetime), and one section of the window for showing the resulting information about flights.
THE CFE is in contact with the following server for querying and asynchronous retrieving data.

######Server Application (SA)

The SA publish a JMS Topic service aimed to communicate with the CFE on one side (receive end user requests), and on the other side, with some Flight Information Provider Units (FIPU) (publish the user requests in a JMS Topic).
SA must use at least two distinct FIPUs.

######Flight Information Provider Unit (FIPU)

Each FIPU is a connector to third party (real) information provider about flights. When a FIPU receives an user-request from the Topic JMS, FIPU acquires the requested informations consuming REST or SOAP web services (e.g.: https://developer.flightstats.com, http://developer.flightlookup.com/webservices, ask TA for a listing of these webservices).
When a FIPU receives the response from its related webservice, FIPU sends this info to the SA (producing a message in a Topic or Queue JMS) in order to let it be available to CFE.
Hence the CFE can receive information from more than one source (FIPUs) with different timings.

######Alert Feature (AF) (Optional feature)

This SOAP web service provides methods for registration/deregistration of the user-email for receiving an email-alert for a specific flight arriving at an airport on the given day.
Both web methods are called by the CFE via its GUI.
E.g. registerArrivalAlarm("31-AUG-2014", "<carrier>", "<flightNumber>", "someone@unibz.it")

Alternatively (and probably more useful!), the service could provide an email-alert when the price for a flight drops below a certain threshold.
E.g. registerPriceAlarm("31-AUG-2014", "MUC-SFO", "750.00", "someone@unibz.it")

Technologies to be used
CFE: GUI developed in Java (using Swing) and the Eclipse environment.
SA: Instance of JBoss Application Server, version 7.1.1 final.
The application is developed in Java using Eclipse environment and JBoss tools (vers. 4.1.2 final).
FIPU: java and Eclipse.

#####Milestones
######8/5/2014, milestone 1

The objective is providing a skeleton of the JMS Topic on JBoss. A simple implementation of Topic producer and Consumer is provided.
Also at least one simple webservice client (the core of a FIPU) has been implemented.

######22/5/2014, milestone 2

The implementation of the JMS is almost complete. At least one FIPU and the CFE have been implemented.
At this state of the art CFE can send messages to SA, messages are received by FIPUs, FIPU can consume its webservice and send back retrived information to SA as well as CFE can show this information on its GUI.

######29/5/2014 Load test

During this lab some load test will be performed in order to find weaknesses and refine systems.
Load tests will be done using JMeter tool.

######6/6/2014 Final release

Projects are delivered in an appropriate Delivery package (CD or USB pen). Including:
Javadoc,
User manual,
Installation manual,
Sufficient Unit test.


FINAL PRESENTATION
On july 3, after the written exam, both project I and II have to be presented and hand delivered.
