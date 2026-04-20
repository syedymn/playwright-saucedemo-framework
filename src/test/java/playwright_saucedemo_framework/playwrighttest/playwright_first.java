package playwright_saucedemo_framework.playwrighttest;

import com.microsoft.playwright.*;
import org.testng.annotations.Test;

public class playwright_first  {
    @Test
    public void verifyLoginPageLoads() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://www.saucedemo.com");
            System.out.println("Title: " + page.title());
            browser.close();
        }
    }
}