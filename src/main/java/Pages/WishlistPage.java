package Pages;

import com.microsoft.playwright.Page;
import Utils.LogsUtil;
import com.microsoft.playwright.options.AriaRole;

public class WishlistPage extends BasePage {

    public WishlistPage(Page page) {
        super(page, "WishlistPage");
        LogsUtil.info("WishlistPage initialized.");
    }

    /**************** Navigation ****************/

    public WishlistPage navigateToWishlist() {
        page.navigate(jsonFile.getJsonData("url"));
        LogsUtil.info("Navigated to Wishlist: " + jsonFile.getJsonData("url"));
        return this;
    }

    public HomePage backToHomePage() {
        locator.byTestId(jsonFile.getJsonData("header.homeLink")).click();
        LogsUtil.info("Navigated back to Home from Wishlist");
        page.waitForTimeout(1000);
        return new HomePage(page);
    }

    public CartPage goToCart() {
        locator.byTestId(jsonFile.getJsonData("header.cartLink")).click();
        LogsUtil.info("Navigated to Cart from Wishlist");
        return new CartPage(page);
    }

    public HomePage browseProducts() {
        locator.byText(jsonFile.getJsonData("pageElements.browseProductsText")).click();
        LogsUtil.info("Clicked Browse Products");
        return new HomePage(page);
    }

    /**************** Wishlist Item Operations ****************/

    public WishlistPage addItemToCart(int index) {
        locator.byRole(AriaRole.BUTTON, jsonFile.getJsonData("item.addToCart"), true)
                .nth(index).click();
        LogsUtil.info("Added item at index " + index + " to cart from wishlist");
        page.waitForTimeout(1000);
        return this;
    }

    public WishlistPage deleteItemFromWishlist(int index) {
        locator.byCss(jsonFile.getJsonData("item.deleteButton")).nth(index).click();
        LogsUtil.info("Deleted item at index " + index + " from wishlist");
        page.waitForTimeout(1000);
        return this;
    }

    /**************** State Queries ****************/

    public boolean isOnWishlistPage() {
        boolean result = page.url().contains("/wishlist");
        if (result) LogsUtil.info("isOnWishlistPage: true");
        else        LogsUtil.error("isOnWishlistPage: false — URL: " + page.url());
        return result;
    }

    public boolean isWishlistEmpty() {
        boolean result = locator.byText("Your Wishlist is Empty").isVisible();
        if (result) LogsUtil.info("isWishlistEmpty: true");
        else        LogsUtil.error("isWishlistEmpty: false — wishlist has items");
        return result;
    }

    public String getWishlistTitle() {
        String title = locator.byRole(AriaRole.HEADING, "My Wishlist", false)
                .textContent().trim();
        LogsUtil.info("getWishlistTitle: " + title);
        return title;
    }

    public boolean isBrowseProductsButtonVisible() {
        boolean result = locator.byText("Browse Products").isVisible();
        if (result) LogsUtil.info("isBrowseProductsButtonVisible: true");
        else        LogsUtil.error("isBrowseProductsButtonVisible: false");
        return result;
    }

    public int getWishlistItemCount() {
        int count = locator.locator(jsonFile.getJsonData("item.selector")).count();
        LogsUtil.info("getWishlistItemCount: " + count);
        return count;
    }

    public boolean isItemPresentInWishlist(String itemName) {
        boolean result = locator.byText(itemName).isVisible();
        if (result) LogsUtil.info("isItemPresentInWishlist: '" + itemName + "' found");
        else        LogsUtil.error("isItemPresentInWishlist: '" + itemName + "' NOT found");
        return result;
    }

    public String getItemName(int index) {
        String name = locator.locator(jsonFile.getJsonData("item.selector"))
                .nth(index)
                .locator("h3, h2, p.font-medium, [class*='font-medium']")
                .first().textContent().trim();
        LogsUtil.info("getItemName[" + index + "]: " + name);
        return name;
    }

    public String getItemPrice(int index) {
        String price = locator.locator(jsonFile.getJsonData("item.selector"))
                .nth(index)
                .locator("[class*='font-bold'], [class*='price'], p:has-text('$')")
                .first().textContent().trim();
        LogsUtil.info("getItemPrice[" + index + "]: " + price);
        return price;
    }
}
