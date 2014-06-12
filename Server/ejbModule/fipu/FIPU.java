package fipu;

import java.util.Date;

import common.ServerResponse;

// abstract class to be extended depending on all the different 
// web services that are going to be supported

public abstract class FIPU {
	
	// sends the request, process it, and returns a response for the client
	public abstract ServerResponse requestResponse(String fromCode, String toCode, Date date);
}
