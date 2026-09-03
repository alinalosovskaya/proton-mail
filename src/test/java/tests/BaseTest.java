package tests;

import config.ConfigReader;
import driver.DriverFactory;
import listeners.TestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.time.Duration;
@Listeners(TestListener.class)
public class BaseTest {

    protected WebDriver driver;

    protected static final String LOGIN_URL = ConfigReader.baseUrl();

    protected static final String TEST_EMAIL =
            getRequiredEnv("PROTON_TEST_EMAIL");

    protected static final String TEST_PASSWORD =
            getRequiredEnv("PROTON_TEST_PASSWORD");

    private static String getRequiredEnv(String name) {
        String value = System.getProperty(name, System.getenv(name));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required environment variable/system property: " + name
                            + ". Set it before running the tests (do not hardcode credentials)."
            );
        }
        return value;
    }

    @BeforeMethod
    public void setUp() {

        String browser = ConfigReader.browser();

        driver = DriverFactory.createDriver(browser);

        driver.manage().window().maximize();

        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(30)
        );

        driver.manage().timeouts().implicitlyWait(
                Duration.ZERO
        );
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}