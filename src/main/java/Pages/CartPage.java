package Pages;

import com.microsoft.playwright.Page;
import Utils.LogsUtil;
import com.microsoft.playwright.options.AriaRole;

public class CartPage extends BasePage {

    public CartPage(Page page) {
        super(page, "CartPage");
        LogsUtil.info("CartPage initialized.");
    }

    /**************** Navigation ****************/

    public CartPage navigateToCart() {
        page.navigate(jsonFile.getJsonData("url"));
        LogsUtil.info("Navigated to Cart Page: " + jsonFile.getJsonData("url"));
        return this;
    }

    /**************** Cart Item Operations ****************/

    public CartPage updateQuantity(int itemIndex) {
        locator.locator(jsonFile.getJsonData("increaseQuantity.testId")).nth(itemIndex).click();
        LogsUtil.info("Increased quantity for item at index: " + itemIndex);
        return this;
    }

    public CartPage decreaseQuantity(int itemIndex) {
        locator.locator(jsonFile.getJsonData("decreaseQuantity.testId")).nth(itemIndex).click();
        LogsUtil.info("Decreased quantity for item at index: " + itemIndex);
        return this;
    }

    public CartPage removeItem(int itemIndex) {
        locator.byRole(AriaRole.BUTTON, jsonFile.getJsonData("removeItem.ariaLabel"), false)
                .nth(itemIndex).click();
        LogsUtil.info("Removed item at index: " + itemIndex);
        return this;
    }

    /**************** Action Methods ****************/

    public CheckoutPage proceedToCheckout() {
        locator.byRole(AriaRole.BUTTON, jsonFile.getJsonData("pageElements.checkoutButton.text"), false)
                .click();
        LogsUtil.info("Proceeded to checkout");
        return new CheckoutPage(page);
    }

    public HomePage continueShopping() {
        locator.byRole(AriaRole.BUTTON, jsonFile.getJsonData("pageElements.continueShoppingButton.text"), false)
                .click();
        LogsUtil.info("Continued shopping");
        return new HomePage(page);
    }

    public HomePage startShopping() {
        locator.byText(jsonFile.getJsonData("pageElements.startShoppingButton.text")).click();
        LogsUtil.info("Started shopping");
        return new HomePage(page);
    }

    /**************** Cart Summary ****************/

    public String getTotalPrice() {
        String total = locator.byTestId(jsonFile.getJsonData("totalPrice.testId")).textContent();
        LogsUtil.info("Cart total price: " + total);
        return total;
    }

    public String getItemPrice(int itemIndex) {
        String price = locator.locator(jsonFile.getJsonData("item.selector"))
                .nth(itemIndex)
                .locator(jsonFile.getJsonData("item.priceSelector"))
                .first().textContent();
        LogsUtil.info("Item " + itemIndex + " price: " + price);
        return price;
    }

    public String getItemName(int itemIndex) {
        String name = locator.locator(jsonFile.getJsonData("item.selector"))
                .nth(itemIndex)
                .locator(jsonFile.getJsonData("item.nameSelector"))
                .first().textContent();
        LogsUtil.info("Item " + itemIndex + " name: " + name);
        return name;
    }

    public int getItemQuantity(int itemIndex) {
        String quantityText = locator.locator(jsonFile.getJsonData("item.selector"))
                .nth(itemIndex)
                .locator(jsonFile.getJsonData("quantity.testId"))
                .first().textContent().trim();
        int quantity = Integer.parseInt(quantityText);
        LogsUtil.info("Item " + itemIndex + " quantity: " + quantity);
        return quantity;
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        int itemCount = getDisplayedItemsCount();
        for (int i = 0; i < itemCount; i++) {
            double price    = Double.parseDouble(getItemPrice(i).replaceAll("[^0-9.]", "").trim());
            int    quantity = getItemQuantity(i);
            subtotal += price * quantity;
        }
        LogsUtil.info("Calculated subtotal: " + String.format("%.2f", subtotal));
        return subtotal;
    }

    /**************** State Queries ****************/

    public boolean isCartPageDisplayed() {
        boolean result = locator.byTestId(jsonFile.getJsonData("pageElements.cartPage")).isVisible();
        if (result) LogsUtil.info("isCartPageDisplayed: true");
        else        LogsUtil.error("isCartPageDisplayed: false");
        return result;
    }

    public boolean isCartEmpty() {
        boolean result = locator.byTestId(jsonFile.getJsonData("pageElements.emptyCartMessage")).isVisible();
        if (result) LogsUtil.info("isCartEmpty: true");
        else        LogsUtil.error("isCartEmpty: false — cart has items");
        return result;
    }

    public boolean isStartShoppingButtonVisible() {
        boolean result = locator.byText(jsonFile.getJsonData("pageElements.startShoppingButton.text")).isVisible();
        if (result) LogsUtil.info("isStartShoppingButtonVisible: true");
        else        LogsUtil.error("isStartShoppingButtonVisible: false");
        return result;
    }

    public int getDisplayedItemsCount() {
        int count = locator.locator(jsonFile.getJsonData("item.selector")).count();
        LogsUtil.info("getDisplayedItemsCount: " + count);
        return count;
    }

    public String getCartPageTitle() {
        String title = locator.byTestId(jsonFile.getJsonData("pageElements.cartTitle")).textContent();
        LogsUtil.info("Cart page title: " + title);
        return title;
    }

    public boolean isItemPresentInCart(String itemName) {
        boolean result = locator.locator(
                        jsonFile.getJsonData("item.nameSelector") + ":has-text('" + itemName + "')")
                .isVisible();
        if (result) LogsUtil.info("isItemPresentInCart: '" + itemName + "' found");
        else        LogsUtil.error("isItemPresentInCart: '" + itemName + "' NOT found");
        return result;
    }

    public boolean isCheckoutButtonEnabled() {
        boolean result = locator.byTestId(jsonFile.getJsonData("pageElements.checkoutButton.testId")).isEnabled();
        if (result) LogsUtil.info("isCheckoutButtonEnabled: true");
        else        LogsUtil.error("isCheckoutButtonEnabled: false");
        return result;
    }

    public boolean isContinueShoppingButtonVisible() {
        boolean result = locator.byTestId(jsonFile.getJsonData("pageElements.continueShoppingButton.testId")).isVisible();
        if (result) LogsUtil.info("isContinueShoppingButtonVisible: true");
        else        LogsUtil.error("isContinueShoppingButtonVisible: false");
        return result;
    }

    public boolean hasQuantityControls(int itemIndex) {
        String uuid        = getCartItemUuid(itemIndex);
        boolean hasIncrease = locator.byTestId("increase-quantity-" + uuid).isVisible();
        boolean hasDecrease = locator.byTestId("decrease-quantity-" + uuid).isVisible();
        boolean result = hasIncrease && hasDecrease;
        if (result) LogsUtil.info("hasQuantityControls: true for item " + itemIndex);
        else        LogsUtil.error("hasQuantityControls: false for item " + itemIndex);
        return result;
    }

    public boolean hasRemoveButton(int itemIndex) {
        String uuid    = getCartItemUuid(itemIndex);
        boolean result = locator.byTestId("remove-item-" + uuid).isVisible();
        if (result) LogsUtil.info("hasRemoveButton: true for item " + itemIndex);
        else        LogsUtil.error("hasRemoveButton: false for item " + itemIndex);
        return result;
    }

    public boolean verifyTotalMatchesSubtotal() {
        double displayedTotal  = Double.parseDouble(getTotalPrice().replaceAll("[^0-9.]", "").trim());
        double calculatedTotal = calculateSubtotal();
        boolean result = Math.abs(displayedTotal - calculatedTotal) < 0.01;
        if (result) LogsUtil.info("verifyTotalMatchesSubtotal: true (" + displayedTotal + " ≈ " + calculatedTotal + ")");
        else        LogsUtil.error("verifyTotalMatchesSubtotal: false — displayed: " + displayedTotal + " calculated: " + calculatedTotal);
        return result;
    }

    public boolean verifyItemDetails(int itemIndex, String expectedName, String expectedPrice, int expectedQuantity) {
        String actualName     = getItemName(itemIndex);
        String actualPrice    = getItemPrice(itemIndex);
        int    actualQuantity = getItemQuantity(itemIndex);
        boolean result = actualName.equals(expectedName)
                && actualPrice.equals(expectedPrice)
                && actualQuantity == expectedQuantity;
        if (result) LogsUtil.info("verifyItemDetails: all match for item " + itemIndex);
        else        LogsUtil.error("verifyItemDetails: mismatch — name:" + actualName + " price:" + actualPrice + " qty:" + actualQuantity);
        return result;
    }

    public CartPage waitForCartToLoad() {
        locator.byTestId(jsonFile.getJsonData("pageElements.cart")).waitFor();
        LogsUtil.info("Cart loaded");
        return this;
    }

    /**************** Helpers ****************/

    private String getCartItemUuid(int itemIndex) {
        String testId = locator.locator(jsonFile.getJsonData("item.selector"))
                .nth(itemIndex).getAttribute("data-testid");
        return testId.replace("cart-item-", "");
    }
}
