package test;

import static org.junit.Assert.*;
import gui.Window;

import java.util.Date;

import jms.MessagingService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import common.ClientRequest;

public class MessagingServiceTest {

	@Test
	public void testSendRequest() {
		assertTrue((new MessagingService(new Window())).sendRequest(new ClientRequest("LHR", "MUC", new Date(), 0)));
	}

}
