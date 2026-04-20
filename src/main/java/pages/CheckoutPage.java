package pages;

import com.microsoft.playwright.Page;

public class CheckoutPage {
    private final Page page;

    private final String firstNameField = "#first-name";
    private final String lastNameField = "#last-name";
    private final String postalCodeField = "#postal-code";
    private final String continueButton = "#continue";
    private final String finishButton = "#finish";
    private final String confirmationHeader = ".complete-header";

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public void fillShippingInfo(String firstName, String lastName, String zip) {
        page.locator(firstNameField).fill(firstName);
        page.locator(lastNameField).fill(lastName);
        page.locator(postalCodeField).fill(zip);
    }

    public void clickContinue() {
        page.locator(continueButton).click();
    }

    public void clickFinish() {
        page.locator(finishButton).click();
    }

    public String getConfirmationMessage() {
        return page.locator(confirmationHeader).textContent();
    }

    public boolean isOrderConfirmed() {
        return page.locator(confirmationHeader).isVisible();
    }
}