package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {

    private static final Logger LOGGER = LogManager.getLogger(ScreenshotUtils.class);

    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");

    public static String capture(WebDriver driver, String testName) {

        try {
            Files.createDirectories(SCREENSHOT_DIR);

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            String fileName = testName + "_" + timestamp + ".png";
            Path targetPath = SCREENSHOT_DIR.resolve(fileName);

            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(sourceFile.toPath(), targetPath);

            String absolutePath = targetPath.toAbsolutePath().toString();

            LOGGER.info("Screenshot saved: {}", absolutePath);

            return absolutePath;

        } catch (IOException e) {
            LOGGER.error("Failed to save screenshot for test: {}", testName, e);
            return null;
        }
    }
}