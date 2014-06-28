package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import common.ServerResponse;

public class ServerResponseTest {

	@Test
	public void testServerResponse() {
		String title = "Title";
		String content = "Content";
		ServerResponse response = new ServerResponse(title, content);
		assertEquals(response.getContent(), new ServerResponse("Title", "Content").getContent());
		assertEquals(response.getTitle(), new ServerResponse("Title", "Content").getTitle());
	}

}
