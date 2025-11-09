package com.example.seleniumdemo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

class CampusOnlineTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        // Auto-manage ChromeDriver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Headless is handy for CI or servers. Comment out if you want a visible browser.
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1280,800");
        // Some environments need these:
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        driver = new ChromeDriver(options);        // Headless is handy for CI or servers. Comment out if you want a visible browser.
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1280,800");
        // Some environments need these:
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // rely on explicit waits
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void landingPageLoadsAndHasExpectedTitle() {
        String url = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
        driver.get(url);

        // Wait for the document to be "interactive/complete" by checking a key element or title
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").toString().equals("complete"));

        // Basic smoke check: title contains something reasonable.
        // Adjust the expected substring if the site uses a different language or branding.
        String title = driver.getTitle();
        Assertions.assertFalse(title.isBlank(), "Title should not be blank");
        // Example assertion (feel free to tweak to the actual title text you see locally):
        Assertions.assertTrue(title.toLowerCase().contains("campus")
                        || title.toLowerCase().contains("ulm"),
                "Page title should mention Campus or Ulm. Actual: " + title);
    }

    @Test
    void canSeeMainContentOrLoginElements() {
        String url = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
        driver.get(url);

        // Try to wait for a meaningful element. These are examples—inspect the page and adjust selectors.
        // We first try something generic commonly present (e.g., <main>, nav, or a login form).
        By candidate = By.cssSelector("main, nav, form[action*='login'], #content, .content");
        WebElement root = wait.until(ExpectedConditions.presenceOfElementLocated(candidate));

        // Quick sanity: element is displayed
        Assertions.assertTrue(root.isDisplayed(), "Main or login element should be visible");
    }



      @Test
    void canSeeTopMenuElements() {
        System.out.println("Starting test: canSeeTopMenuElements");
        String url = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
        driver.get(url);

        // Check if selector #wrapper > div.divlinks contains Studentisches Leben, Veranstaltungen, Organisationseinheiten, Studium, Räume und Gebäude, Personen
        By topMenuSelector = By.cssSelector("#wrapper > div.divlinks");
        WebElement topMenu = wait.until(ExpectedConditions.presenceOfElementLocated(topMenuSelector));

        String[] expectedItems = {
                "Studentisches Leben",
                "Veranstaltungen",
                "Organisationseinheiten",
                "Studium",
                "Räume und Gebäude",
                "Personen"
        };

        for (String item : expectedItems) {
            System.out.println("Checking for top menu item: " + item);
            Assertions.assertTrue(topMenu.getText().contains(item), "Top menu should contain: " + item);
            System.out.println("Found top menu item: " + item);
        }
    }
}
