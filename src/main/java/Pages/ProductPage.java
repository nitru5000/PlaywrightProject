package Pages;

import com.microsoft.playwright.Page;
import Utils.LogsUtil;

public class ProductPage extends BasePage {

    public ProductPage(Page page) {
        super(page, "ProductPage");
        LogsUtil.info("ProductPage initialized");
    }

    /**************** Product Actions ****************/

    public ProductPage addToCart() {
        locator.byTestId(jsonFile.getJsonData("addToCart_Button")).click();
        LogsUtil.info("Added product to cart");
        return this;
    }

    public ProductPage addToWishlist() {
        locator.byTestId(jsonFile.getJsonData("wishList_Button")).click();
        LogsUtil.info("Added product to wishlist");
        page.waitForTimeout(500);
        return this;
    }

    public ProductPage compareProduct() {
        locator.byTestId(jsonFile.getJsonData("compare_Button")).click();
        LogsUtil.info("Added product to compare list");
        page.waitForTimeout(500);
        return this;
    }

    public ProductPage nextImage() {
        locator.byTestId(jsonFile.getJsonData("nextImage_Button")).click();
        LogsUtil.info("Navigated to next image");
        page.waitForTimeout(500);
        return this;
    }

    public ProductPage previousImage() {
        locator.byTestId(jsonFile.getJsonData("previousImage_Button")).click();
        LogsUtil.info("Navigated to previous image");
        page.waitForTimeout(500);
        return this;
    }

    /**************** Email a Friend ****************/

    public ProductPage emailAFriend() {
        locator.byText(jsonFile.getJsonData("emailAFriendText")).click();
        LogsUtil.info("Clicked Email a Friend");
        // Wait for dialog — confirmed title "Share with a Friend" from screenshot
        locator.byText(jsonFile.getJsonData("shareWithFriendTitle")).waitFor();
        return this;
    }

    public ProductPage fillFriendEmail(String friendEmail) {
        locator.byPlaceholder(jsonFile.getJsonData("friendEmailPlaceholder"))
                .type(friendEmail, new com.microsoft.playwright.Locator.TypeOptions().setDelay(70));
        LogsUtil.info("Entered friend email: " + friendEmail);
        return this;
    }

    public ProductPage fillYourEmail(String yourEmail) {
        locator.byPlaceholder(jsonFile.getJsonData("yourEmailPlaceholder"))
                .type(yourEmail, new com.microsoft.playwright.Locator.TypeOptions().setDelay(70));
        LogsUtil.info("Entered your email: " + yourEmail);
        return this;
    }

    public ProductPage fillMessage(String message) {
        // Confirmed from screenshot: placeholder is "Check out this product!" (lowercase t)
        locator.byPlaceholder(jsonFile.getJsonData("messagePlaceholder"))
                .type(message, new com.microsoft.playwright.Locator.TypeOptions().setDelay(70));
        LogsUtil.info("Entered message: " + message);
        return this;
    }

    public ProductPage sendEmail() {
        // Confirmed from screenshot: button text is "Send Email"
        locator.byText(jsonFile.getJsonData("sendEmailText")).click();
        LogsUtil.info("Clicked Send Email");
        page.waitForTimeout(500);
        return this;
    }

    /**************** Related Products ****************/

    public ProductPage addRelatedProductToCart(int index) {
        locator.byText(jsonFile.getJsonData("addToCartText")).nth(index).click();
        LogsUtil.info("Added related product at index " + index + " to cart");
        return this;
    }

    public ProductPage addRelatedProductToWishlist(int index) {
        locator.byText("Save").nth(index).click();
        LogsUtil.info("Added related product at index " + index + " to wishlist");
        return this;
    }

    public ProductPage compareRelatedProducts(int index) {
        locator.byText("Compare").nth(index).click();
        LogsUtil.info("Added related product at index " + index + " to compare");
        return this;
    }

    /**************** Navigation ****************/

    public HomePage backToProducts() {
        locator.byText(jsonFile.getJsonData("backToProductsText")).click();
        LogsUtil.info("Clicked Back to Products");
        page.waitForTimeout(500);
        return new HomePage(page);
    }

    /**************** State Queries ****************/

    public boolean isEmailDialogVisible() {
        boolean result = locator.byText(jsonFile.getJsonData("shareWithFriendTitle")).isVisible();
        if (result) LogsUtil.info("isEmailDialogVisible: true");
        else        LogsUtil.error("isEmailDialogVisible: false");
        return result;
    }

    public boolean isOnProductPage() {
        locator.locator("h1").first().waitFor();
        boolean result = page.url().contains("/product/");
        if (result) LogsUtil.info("isOnProductPage: true — URL: " + page.url());
        else        LogsUtil.error("isOnProductPage: false — URL: " + page.url());
        return result;
    }

    public String getProductName() {
        locator.locator("h1").first().waitFor();
        String name = locator.locator("h1").first().textContent().trim();
        LogsUtil.info("getProductName: " + name);
        return name;
    }

    public String getProductPrice() {
        String price = locator.locator("[data-testid='product-price']").textContent().trim();
        LogsUtil.info("getProductPrice: " + price);
        return price;
    }

    public boolean isInStock() {
        // Confirmed from screenshot: plain text "In Stock"
        boolean result = page.locator("text='In Stock'").isVisible();
        if (result) LogsUtil.info("isInStock: true");
        else        LogsUtil.error("isInStock: false");
        return result;
    }

    public boolean isAddToCartButtonVisible() {
        boolean result = locator.byTestId(jsonFile.getJsonData("addToCart_Button")).isVisible();
        if (result) LogsUtil.info("isAddToCartButtonVisible: true");
        else        LogsUtil.error("isAddToCartButtonVisible: false");
        return result;
    }

    public boolean isWishlistButtonVisible() {
        boolean result = locator.byTestId(jsonFile.getJsonData("wishList_Button")).isVisible();
        if (result) LogsUtil.info("isWishlistButtonVisible: true");
        else        LogsUtil.error("isWishlistButtonVisible: false");
        return result;
    }

    public boolean isCompareButtonVisible() {
        boolean result = locator.byTestId(jsonFile.getJsonData("compare_Button")).isVisible();
        if (result) LogsUtil.info("isCompareButtonVisible: true");
        else        LogsUtil.error("isCompareButtonVisible: false");
        return result;
    }

    public boolean isEmailAFriendVisible() {
        boolean result = locator.byText(jsonFile.getJsonData("emailAFriendText")).isVisible();
        if (result) LogsUtil.info("isEmailAFriendVisible: true");
        else        LogsUtil.error("isEmailAFriendVisible: false");
        return result;
    }

    public boolean isCustomerReviewsSectionVisible() {
        boolean result = locator.byText(jsonFile.getJsonData("customerReviewsText")).isVisible();
        if (result) LogsUtil.info("isCustomerReviewsSectionVisible: true");
        else        LogsUtil.error("isCustomerReviewsSectionVisible: false");
        return result;
    }

    public boolean isNoReviewsMessageVisible() {
        boolean result = locator.byText(jsonFile.getJsonData("noReviewsYetText")).isVisible();
        if (result) LogsUtil.info("isNoReviewsMessageVisible: true");
        else        LogsUtil.error("isNoReviewsMessageVisible: false");
        return result;
    }

    public boolean isSignInToReviewButtonVisible() {
        boolean result = locator.byText(jsonFile.getJsonData("signInToReviewText")).isVisible();
        if (result) LogsUtil.info("isSignInToReviewButtonVisible: true");
        else        LogsUtil.error("isSignInToReviewButtonVisible: false");
        return result;
    }

    public boolean isShareYourExperienceVisible() {
        boolean result = locator.byText(jsonFile.getJsonData("shareYourExperienceText")).isVisible();
        if (result) LogsUtil.info("isShareYourExperienceVisible: true");
        else        LogsUtil.error("isShareYourExperienceVisible: false");
        return result;
    }
}
