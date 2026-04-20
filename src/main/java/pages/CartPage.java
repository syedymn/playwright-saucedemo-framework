package pages;

import com.microsoft.playwright.Page;

public class CartPage {
    private final Page page;

    private final String cartItems = ".cart_item";
    private final String checkoutButton = "#checkout";
    private final String removeButton = "[data-test^='remove']";

    public CartPage(Page page) {
        this.page = page;
    }

    public int getCartItemCount() {
        return page.locator(cartItems).count();
    }

    public boolean isItemInCart(String productName) {
        return page.locator(cartItems)
            .filter(new com.microsoft.playwright.Locator.FilterOptions()
                .setHasText(productName))
            .isVisible();
    }

    public void proceedToCheckout() {
        page.locator(checkoutButton).click();
    }
}