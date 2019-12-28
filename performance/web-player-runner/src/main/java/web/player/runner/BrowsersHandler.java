package web.player.runner;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BrowsersHandler {

    private static final Logger log = LoggerFactory.getLogger(BrowsersHandler.class);

    private String browserName;
    private String url;
    Integer instances;
    private Duration runningTimeDuration;

    private List<WebDriver> drivers;

    public BrowsersHandler(String browserName, String url, Integer instances, Duration runningTimeDuration) {
        this.browserName = browserName.toLowerCase();
        this.url = url;
        this.instances = instances;
        this.runningTimeDuration = runningTimeDuration;

        this.drivers = new ArrayList<>();

        setupWebDriver();
        closeWebDriversOnShutdown();
    }

    private void setupWebDriver() {
        WebDriverManager manager;
        switch (browserName) {
            case "firefox":
                manager = WebDriverManager.firefoxdriver();
                break;
            case "edge":
                manager = WebDriverManager.edgedriver();
                break;
            case "ie":
                manager = WebDriverManager.iedriver();
                break;
            case "chrome":
            default:
                manager = WebDriverManager.chromedriver();
                break;
        }
        File currentDir = new File("");
        manager.targetPath(currentDir.getAbsolutePath()).setup();
    }

    private WebDriver createWebDriver() {
        switch (browserName) {
            case "firefox":
                return new FirefoxDriver();
            case "edge":
                return new EdgeDriver();
            case "ie":
                return new InternetExplorerDriver();
            case "chrome":
            default:
                return new ChromeDriver();
        }
    }

    private void closeWebDriversOnShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down");
            drivers.forEach(WebDriver::close);
        }));
    }

    public void run() {
        log.info("Launching {} instances", instances);
        for (int i = 0; i < instances; i++) {
            launchInstance(i);
        }

        try {
            log.info("Sleeping during {}", runningTimeDuration.toString());
            Thread.sleep(runningTimeDuration.toMillis());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private void launchInstance(Integer instanceNumber) {
        WebDriver driver = createWebDriver();
        drivers.add(driver);

        driver.manage().window().setPosition(new Point(instanceNumber * 40,200));
        driver.manage().window().setSize(new Dimension(700,700));

        driver.get(url);

        By playButtonLocator = By.id("play");

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(playButtonLocator));

        driver.findElement(playButtonLocator).click();
    }

}
