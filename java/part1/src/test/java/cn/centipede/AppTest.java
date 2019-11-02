package cn.centipede;

// import cn.centipede.numpy.NDArrayTest;
// import cn.centipede.numpy.NumpyTest;
import junit.framework.TestCase;
// import org.junit.runners.Suite;
// import org.junit.runners.Suite.SuiteClasses;
// import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
//@RunWith(Suite.class)
//@SuiteClasses({NDArrayTest.class,NumpyTest.class})
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super( testName );
    }

    public void testApp() {
        System.out.println("App test");
    }
}
