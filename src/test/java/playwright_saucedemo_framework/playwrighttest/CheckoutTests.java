package playwright_saucedemo_framework.playwrighttest;

import playwright_saucedemo_framework.playwrighttest.BaseTest;
import utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;

public class CheckoutTests extends BaseTest {

    @Test(description = "End-to-end purchase flow")
    public void testCompleteCheckoutFlow() {
        LoginPage loginPage = new LoginPage(getPage());
        loginPage.navigate(ConfigReader.get("base.url"));
        loginPage.login(
            ConfigReader.get("standard.user"),
            ConfigReader.get("password")
        );

        InventoryPage inventoryPage = new InventoryPage(getPage());
        Assert.assertTrue(inventoryPage.isLoaded());
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(inventoryPage.getCartCount(), "1");

        inventoryPage.goToCart();
        CartPage cartPage = new CartPage(getPage());
        Assert.assertEquals(cartPage.getCartItemCount(), 1);

        cartPage.proceedToCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(getPage());
        checkoutPage.fillShippingInfo("John", "Doe", "10001");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderConfirmed());
    }
}