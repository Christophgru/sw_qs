#[tokio::main]
async fn main() {}

#[cfg(test)]
mod test {
    use std::{collections::HashSet, time::Duration};
    use thirtyfour::{
        By, ChromeCapabilities, ChromiumLikeCapabilities, DesiredCapabilities, Key, TypingData, WebDriver, WebElement, common::command::BySelector, error::WebDriverResult, prelude::ElementQueryable
    };
    use tokio::time::sleep;

    //Get capabilities of Chrome browser
    fn get_capabilities() -> ChromeCapabilities {
        let mut caps = DesiredCapabilities::chrome();
        caps.add_arg("--start-maximized").unwrap();
        caps.add_arg("--headless=disable").unwrap();
        caps.into()
    }

    //Get URL of Windows host
    fn get_driver_url() -> String {
        "http://172.28.208.1:9515".to_string()
    }

    //Connect to Chromedriver and return it
    async fn connect(driver_url: &str, caps: ChromeCapabilities, website: &str) -> WebDriver {
        let driver: WebDriver = WebDriver::new(driver_url, caps).await.unwrap();
        driver.maximize_window().await.unwrap();
        driver.goto(website).await.unwrap();
        driver
    }

    // expected: &[&str] passt zu deinem EXPECTED aus Literalen
    pub async fn check_topmenu_contents(
        driver: WebDriver,
        expected: &[&str],
        by_css: By,
    ) -> WebDriverResult<()> {
        let items = driver.find_all(by_css).await?;
        assert!(!items.is_empty(), "No Topmenu found.");

        let missing: Vec<String> = compare_webelement_texts_to_strings(items, expected).await;

        assert!(
            missing.is_empty(),
            "Missing Menu entries: {:?}",
            missing
        );

        driver.quit().await?;
        Ok(())
    }

    pub async fn elements_to_strings(elements: Vec<WebElement>) -> Vec<String> {
        let mut labels = Vec::with_capacity(elements.len());
        for el in elements {
            labels.push(el.text().await.unwrap_or_default());
        }
        labels
    }

    // Liefert fehlende erwartete Texte als Strings zurück (praktisch für Tests)
    pub async fn compare_webelement_texts_to_strings(
        elements: Vec<WebElement>,
        expected: &[&str],
    ) -> Vec<String> {
        let labels = elements_to_strings(elements).await; // Vec<String>
        let found: HashSet<&str> = labels.iter().map(|s| s.as_str()).collect();
        let expected_set: HashSet<&str> = expected.iter().copied().collect();

        expected_set
            .difference(&found)
            .map(|s| s.to_string())
            .collect()
    }

    #[tokio::test]
    async fn check_topmenu_german() {
        const EXPECTED: &[&str] = &[
            "Studentisches Leben",
            "Veranstaltungen",
            "Organisationseinheiten",
            "Studium",
            "Räume und Gebäude",
            "Personen",
        ];

        let by_css: By = By::Css("a[class^='links2']");

        let website: &str = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
        let driver: WebDriver = connect(&get_driver_url(), get_capabilities(), website).await;

        check_topmenu_contents(driver, &EXPECTED, by_css).await.unwrap();
    }

    #[tokio::test]
    async fn check_topmenu_english() {
        const EXPECTED: &[&str] = &[
            "Student's Corner",
            "Courses",
            "Organizational units",
            "Study",
            "Facilities",
            "Members",
        ];

        let by_css: By = By::Css("a[class^='links2']");

        let website: &str = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
        let driver: WebDriver = connect(&get_driver_url(), get_capabilities(), website).await;

        let query: thirtyfour::extensions::query::ElementQuery = driver
            .query(By::Css("img[title='Switch to english language']"))
            .or(By::Css("img[alt='Switch to english language']"))
            .or(By::Css("img[src$='/QIS/images/flag_en.svg']"));
        assert!(query.exists().await.unwrap());
        let flag = query.first().await.unwrap();
        flag.scroll_into_view().await.unwrap();
        sleep(Duration::from_millis(100)).await;
        flag.click().await.unwrap();

        check_topmenu_contents(driver, EXPECTED, by_css).await.unwrap();
    }

    #[tokio::test]
    async fn check_course_can_be_searched_for() {
        let website: &str = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
        let driver: WebDriver = connect(&get_driver_url(), get_capabilities(), website).await;

        let query_veranstaltungen = driver
            .query(By::ClassName("links2"))
            .with_text("Veranstaltungen");
        assert!(
            query_veranstaltungen.exists().await.unwrap(),
            "Veranstaltungen not found"
        );

        let veranstaltungen = query_veranstaltungen.single().await.unwrap();
        veranstaltungen.click().await.unwrap();

        let query_suche = driver
            .query(By::ClassName("auflistung"))
            .with_text("Suche nach Veranstaltungen");
        assert!(
            query_suche.exists().await.unwrap(),
            "Suche nach Veranstaltungen not found"
        );

        let suche = query_suche.single().await.unwrap();
        suche.click().await.unwrap();

        let query_titel = driver.query(By::Id("veranstaltung.dtxt"));
        assert!(
            query_titel.exists().await.unwrap(),
            "Veranstaltung textbox not found"
        );

        let titel_textbox = query_titel.single().await.unwrap();
        titel_textbox
            .send_keys("Softwarequalitätssicherung" + Key::Enter)
            .await
            .unwrap();

        let query_id = driver
            .query(By::ClassName("mod_n_odd"))
            .with_text("CS7251.000");
        assert!(query_id.exists().await.unwrap(), "course ID not found");

        let query_course_link = driver
            .query(By::ClassName("regular"))
            .with_text("Softwarequalitätssicherung");
        assert!(
            query_course_link.exists().await.unwrap(),
            "course Link not found"
        );

        let course_link = query_course_link.single().await.unwrap();
        course_link.click().await.unwrap();

        let query_lecturers = driver.query(By::Css("[headers=\"persons_1\"]"));
        assert!(
            query_lecturers.exists().await.unwrap(),
            "lecturers not found"
        );

        let lecturers = query_lecturers.all_from_selector().await.unwrap();
        const LECTURERS: &[&str] = &[
            "Raschke, Alexander , Dr.",
            "Neumüller, Denis",
        ];

        let missing = compare_webelement_texts_to_strings(lecturers, LECTURERS).await;

        assert!(
            missing.is_empty(),
            "Missing lecturers: {:?}",
            missing
        );

        let query_id = driver
            .query(By::ClassName("mod_n"))
            .with_text("CS7251.000");
        assert!(query_id.exists().await.unwrap(), "course ID not found");

        driver.quit().await.unwrap();
    }

    #[tokio::test]
    async fn test_login_input_fields() {
        let website: &str = "https://campusonline.uni-ulm.de/qislsf/rds?state=user&type=0";
        let driver: WebDriver = connect(&get_driver_url(), get_capabilities(), website).await;

        let query_username_input = driver.query(By::ClassName("input_login")).with_id("asdf");
        assert!(query_username_input.exists().await.unwrap(), "username login input not found");
    
        let query_username_input = driver.query(By::ClassName("input_login")).with_id("fdsa");
        assert!(query_username_input.exists().await.unwrap(), "password login input not found")
    }
}
