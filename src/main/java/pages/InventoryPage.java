package pages;

import com.microsoft.playwright.Page;

public class InventoryPage {
    private final Page page;

    private final String pageTitle = ".title";
    private final String inventoryItems = ".inventory_item";
    private final String cartIcon = ".shopping_cart_link";
    private final String cartBadge = ".shopping_cart_badge";

    public InventoryPage(Page page) {
        this.page = page;
    }

    public String getPageTitle() {
        return page.locator(pageTitle).textContent();
    }

    public boolean isLoaded() {
        return page.locator(pageTitle).isVisible();
    }

    public int getProductCount() {
        return page.locator(inventoryItems).count();
    }

    public void addProductToCart(String productName) {
        page.locator(".inventory_item")
            .filter(new com.microsoft.playwright.Locator.FilterOptions()
                .setHasText(productName))
            .locator("button")
            .click();
    }

    public String getCartCount() {
        return page.locator(cartBadge).textContent();
    }

    public void goToCart() {
        page.locator(cartIcon).click();
    }
}