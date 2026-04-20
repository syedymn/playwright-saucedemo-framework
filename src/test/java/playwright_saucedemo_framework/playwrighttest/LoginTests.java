package playwright_saucedemo_framework.playwrighttest;


import playwright_saucedemo_framework.playwrighttest.BaseTest;
import utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.InventoryPage;
import pages.LoginPage;

public class LoginTests extends BaseTest {

    @Test(description = "Valid user can login successfully")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(getPage());
        loginPage.navigate(ConfigReader.get("base.url"));
        loginPage.login(
            ConfigReader.get("standard.user"),
            ConfigReader.get("password")
        );
        InventoryPage inventoryPage = new InventoryPage(getPage());
        Assert.assertTrue(inventoryPage.isLoaded());
        Assert.assertEquals(inventoryPage.getPageTitle(), "Products");
    }

    @Test(description = "Locked out user sees error message")
    public void testLockedOutUserLogin() {
        LoginPage loginPage = new LoginPage(getPage());
        loginPage.navigate(ConfigReader.get("base.url"));
        loginPage.login(
            ConfigReader.get("locked.user"),
            ConfigReader.get("password")
        );
        Assert.assertTrue(loginPage.isErrorDisplayed());
        Assert.assertTrue(
            loginPage.getErrorMessage().contains("locked out")
        );
    }

    @Test(description = "Empty credentials shows error")
    public void testEmptyCredentials() {
        LoginPage loginPage = new LoginPage(getPage());
        loginPage.navigate(ConfigReader.get("base.url"));
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.isErrorDisplayed());
    }

    @Test(description = "Wrong password shows error")
    public void testInvalidPassword() {
        LoginPage loginPage = new LoginPage(getPage());
        loginPage.navigate(ConfigReader.get("base.url"));
        loginPage.login(
            ConfigReader.get("standard.user"),
            "wrongpassword"
        );
        Assert.assertTrue(loginPage.isErrorDisplayed());
    }
}