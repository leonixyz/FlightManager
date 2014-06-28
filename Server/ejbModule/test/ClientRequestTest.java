package test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import common.ClientRequest;

public class ClientRequestTest {

	@Test
	public void testToString() {
		Date date = new Date();
		String shallBe = "ClientRequest [departure=MUC, arrival=LHR, date=" + date + ", desiredWebservice=0]";
		ClientRequest theObject = new ClientRequest("MUC","LHR", date, 0);
		assertEquals(shallBe, theObject.toString());
	}

}
