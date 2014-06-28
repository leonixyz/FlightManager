package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClientRequestTest.class, MessagingServiceTest.class,
		ServerResponseTest.class })
public class AllTests {

}
