package com.example.seleniumdemo;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

//mvn -q -Dtest=CampusOnlineTest test

class CampusOnlineTest {

        private WebDriver driver;
        private WebDriverWait wait;

        @BeforeEach
        void setUp() {
                // Auto-manage ChromeDriver
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();
                // Headless is handy for CI or servers. Comment out if you want a visible
                // browser.
                options.addArguments("--headless=new");// Headless is handy for CI or servers. Comment out if you want a
                                                       // visible
                options.addArguments("--window-size=800,800");
                // Some environments need these:
                options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

                driver = new ChromeDriver(options);

                driver = new ChromeDriver(options);
                wait = new WebDriverWait(driver, Duration.ofSeconds(5));
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

                // Wait for the document to be "interactive/complete" by checking a key element
                // or title
                wait.until(d -> ((JavascriptExecutor) d)
                                .executeScript("return document.readyState").toString().equals("complete"));

                // Basic smoke check: title contains something reasonable.
                // Adjust the expected substring if the site uses a different language or
                // branding.
                String title = driver.getTitle();
                Assertions.assertFalse(title.isBlank(), "Title should not be blank");
                // Example assertion (feel free to tweak to the actual title text you see
                // locally):
                Assertions.assertTrue(title.toLowerCase().contains("campus")
                                || title.toLowerCase().contains("ulm"),
                                "Page title should mention Campus or Ulm. Actual: " + title);
        }

        @Test
        void canSeeMainContentOrLoginElements() {
                String url = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
                driver.get(url);

                // Try to wait for a meaningful element. These are examples—inspect the page and
                // adjust selectors.
                // We first try something generic commonly present (e.g., <main>, nav, or a
                // login form).
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

                // Check if selector #wrapper > div.divlinks contains Studentisches Leben,
                // Veranstaltungen, Organisationseinheiten, Studium, Räume und Gebäude, Personen
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

        @Test
        void canSeeEnglishTopMenuElements() {
                System.out.println("Starting test: canSeeTopMenuElements");
                String url = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
                driver.get(url);

                // click #wrapper > div.header_hisinone > div.services > a > img
                By langSelector = By.cssSelector("#wrapper > div.header_hisinone > div.services > a > img");
                WebElement langButton = wait.until(ExpectedConditions.elementToBeClickable(langSelector));
                System.out.println("Clicking language button to switch to English");
                langButton.click();
                System.out.println("Clicked language button to switch to English");

                // Check if selector #wrapper > div.divlinks contains Studentisches Leben,
                // Veranstaltungen, Organisationseinheiten, Studium, Räume und Gebäude, Personen
                By topMenuSelector = By.cssSelector("#wrapper > div.divlinks");
                WebElement topMenu = wait.until(ExpectedConditions.presenceOfElementLocated(topMenuSelector));

                String[] expectedItems = {
                                "Student's Corner",
                                "Courses",
                                "Organizational units",
                                "Study",
                                "Facilities",
                                "Members"
                };

                for (String item : expectedItems) {
                        System.out.println("Checking for top menu item: " + item);
                        Assertions.assertTrue(topMenu.getText().contains(item), "Top menu should contain: " + item);
                        System.out.println("Found top menu item: " + item);
                }
        }

        @Test
        void searchForSwQuali() {
                System.out.println("Starting test: searchForSwQuali");
                String url = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
                driver.get(url);

                // click #wrapper > div.header_hisinone > div.services > a > img
                By langSelector = By.cssSelector("#wrapper > div.divlinks > a:nth-child(3)");
                WebElement veranstaltungsButton = wait.until(ExpectedConditions.elementToBeClickable(langSelector));
                System.out.println("Clicking language button to switch to Veranstaltungen");
                veranstaltungsButton.click();
                System.out.println("Clicked language button to switch to Veranstaltungen");
                By verzeichnisSelector = By.cssSelector("#makronavigation > ul > li:nth-child(1) > a");
                WebElement verzeichnisButton = wait.until(ExpectedConditions.elementToBeClickable(verzeichnisSelector));
                System.out.println("Clicking language button to switch to Veranstaltung");
                verzeichnisButton.click();
                System.out.println("Clicked language button to switch to Veranstaltungen");
                // "Suche nach Veranstaltungen"
                By veranstaltungsSelector = By.cssSelector("#makronavigation > ul > li:nth-child(2) > a");
                WebElement veranstaltungsSucheButton = wait
                                .until(ExpectedConditions.elementToBeClickable(veranstaltungsSelector));
                System.out.println("Clicking language button to switch to Veranstaltungssuche");
                veranstaltungsSucheButton.click();
                System.out.println("Clicked language button to switch to Veranstaltungssuche");
                // search for inputbox "document.querySelector("#veranstaltung\\.dtxt")"

                WebElement inputBox = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//*[@id=\"veranstaltung.dtxt\"]")));

                System.out.println("add input keys");
                inputBox.sendKeys("Softwarequalitätssicherung");

                By submitSelector = By
                                .cssSelector("#wrapper > div.divcontent > div.content > div > form > input[type=submit]:nth-child(19)");
                WebElement selectButton = wait.until(ExpectedConditions.presenceOfElementLocated(submitSelector));
                selectButton.click();

                // check for page to contain Module Number "CS7251.000"
                Assertions.assertTrue(wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//td[normalize-space(text())='CS7251.000']")))
                                .isDisplayed(),
                                "Expected the course cell 'CS7251.000' to be visible on the page");

                WebElement click_course_link = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//a[normalize-space(text())='Softwarequalitätssicherung']")));

                System.out.println("Clicking Courselink");
                click_course_link.click();

                Assertions.assertTrue(wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//b[contains(.,'Raschke') and contains(.,'Alexander')]")))
                                .isDisplayed(),
                                "Expected the Raschke to be visible on the page");

                Assertions.assertTrue(wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//b[contains(.,'Neumüller') and contains(.,'Denis')]")))
                                .isDisplayed(),
                                "Expected the Raschke to be visible on the page");
        }

        @Test
        void testLoginAvailable() {

                System.out.println("Starting test: searchForSwQuali");
                String url = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
                driver.get(url);

                WebElement eingabe_benutzer = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//label[normalize-space(text())='Benutzerkennung']/following-sibling::input")));
                Assertions.assertTrue(eingabe_benutzer.isDisplayed(),
                                "Expected the Benutzerkennung input to be visible on the page");
                WebElement eingabe_passwort = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(By.xpath(
                                                "//label[contains(normalize-space(.), 'Passwort')]/following-sibling::input")));
                Assertions.assertTrue(eingabe_passwort.isDisplayed(),
                                "Expected the Passwort input to be visible on the page");
        }
}
