package tests;

import com.epam.reportportal.service.ReportPortal;        // ДОБАВЛЕНО
import config.ConfigReader;
import driver.*;
import listeners.TestListener;
import model.User;
import org.openqa.selenium.OutputType;                    // ДОБАВЛЕНО
import org.openqa.selenium.TakesScreenshot;               // ДОБАВЛЕНО
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;                             // ДОБАВЛЕНО
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.io.File;                                       // ДОБАВЛЕНО
import java.util.Calendar;                                 // ДОБАВЛЕНО
import java.util.Map;

@Listeners({
        TestListener.class,
        com.epam.reportportal.testng.ReportPortalTestNGListener.class
})
public class BaseTest {

    private DriverFactory driverFactory;

    protected WebDriver driver;

    protected static final String LOGIN_URL =
            ConfigReader.getInstance().baseUrl();

    protected static final String TEST_EMAIL =
            getRequiredEnv("PROTON_TEST_EMAIL");

    protected static final String TEST_PASSWORD =
            getRequiredEnv("PROTON_TEST_PASSWORD");

    protected static final User TEST_USER =
            new User(TEST_EMAIL, TEST_PASSWORD);

    private static String getRequiredEnv(String name) {
        String value = System.getProperty(name, System.getenv(name));

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required environment variable/system property: "
                            + name
                            + ". Set it before running the tests (do not hardcode credentials)."
            );
        }

        return value;
    }

    @BeforeMethod
    public void setUp() {

        BrowserDriverRegistry registry = new BrowserDriverRegistry(
                Map.of(
                        "chrome", new ChromeDriverCreator(),
                        "firefox", new FirefoxDriverCreator(),
                        "edge", new EdgeDriverCreator()
                )
        );

        driverFactory = new DriverFactory(registry);

        driver = driverFactory.createDriver(
                ConfigReader.getInstance().browser(),
                ConfigReader.getInstance().isHeadless()
        );

        DriverContext.set(driver);

        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {              // <-- ДОБАВЛЕН ПАРАМЕТР ITestResult

        // ЭТОТ БЛОК ДОБАВЛЕН - если тест упал, делаем скриншот и отправляем в Report Portal
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            try {
                File screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.FILE);

                ReportPortal.emitLog(
                        "Скриншот упавшего теста: " + result.getName(),
                        "ERROR",
                        Calendar.getInstance().getTime(),
                        screenshot
                );
            } catch (Exception e) {
                System.err.println("Не удалось сделать скриншот: " + e.getMessage());
            }
        }
        // КОНЕЦ ДОБАВЛЕННОГО БЛОКА

        if (driver != null) {
            driver.quit();
            DriverContext.remove();
            driver = null;
        }
    }
}