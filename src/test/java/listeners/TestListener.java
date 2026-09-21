package listeners;

import driver.DriverContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestFailure(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        LOGGER.error("Test FAILED: {}", testName, result.getThrowable());

        WebDriver driver = DriverContext.get();

        if (driver != null) {
            ScreenshotUtils.capture(driver, testName);}

        ScreenshotUtils.capture(driver, testName);
    }
}