package pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;

    // Locators
    private final String usernameField = "#user-name";
    private final String passwordField = "#password";
    private final String loginButton = "#login-button";
    private final String errorMessage = "[data-test='error']";

    public LoginPage(Page page) {
        this.page = page;
    }

    public void navigate(String url) {
        page.navigate(url);
    }

    public void enterUsername(String username) {
        page.locator(usernameField).fill(username);
    }

    public void enterPassword(String password) {
        page.locator(passwordField).fill(password);
    }

    public void clickLogin() {
        page.locator(loginButton).click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        return page.locator(errorMessage).textContent();
    }

    public boolean isErrorDisplayed() {
        return page.locator(errorMessage).isVisible();
    }
}