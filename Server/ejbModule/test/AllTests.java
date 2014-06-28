package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClientRequestTest.class, FlightLookupFIPUTest.class,
		FlightStatsFIPUTest.class, RequestListenerTest.class,
		ServerResponseTest.class })
public class AllTests {

}
