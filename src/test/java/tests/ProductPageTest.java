package tests;

import Pages.CartPage;
import Pages.HomePage;
import Pages.ProductPage;
import Pages.WishlistPage;
import base.BaseTest;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductPageTest extends BaseTest {

    private HomePage homePage;

    private static final int    PRODUCT_INDEX = 1;
    private static final String PRODUCT_NAME  = testData.getJsonData("products.index1.name");
    private static final String FRIEND_EMAIL  = testData.getJsonData("emailFriend.friendEmail");
    private static final String YOUR_EMAIL    = testData.getJsonData("emailFriend.yourEmail");
    private static final String MESSAGE       = testData.getJsonData("emailFriend.message");

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        homePage = new HomePage(getPage());
        homePage.navigateToHomePage().acceptCookies();
    }

    // ── Page Structure ────────────────────────────────────────────────────────

    @Test(description = "TC-01 | Clicking product navigates to product detail page")
    public void testNavigateToProductPage() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        assertThat(productPage.isOnProductPage())
                .as("Expected URL to contain '/product/'. Got: " + productPage.getCurrentUrl())
                .isTrue();
    }

    @Test(description = "TC-02 | Product page displays correct product name")
    public void testProductName() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        assertThat(productPage.getProductName())
                .as("Expected product name to contain '" + PRODUCT_NAME + "'")
                .contains(PRODUCT_NAME);
    }

    @Test(description = "TC-03 | Product page shows In Stock status")
    public void testProductInStock() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        assertThat(productPage.isInStock())
                .as("Expected 'In Stock' to be visible on product page")
                .isTrue();
    }

    // ── Action Buttons ────────────────────────────────────────────────────────
    // Soft — all 4 buttons are completely independent of each other

    @Test(description = "TC-04 | All action buttons are visible on product page")
    public void testActionButtonsVisible() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(productPage.isAddToCartButtonVisible())
                    .as("Expected Add to Cart button to be visible")
                    .isTrue();
            soft.assertThat(productPage.isWishlistButtonVisible())
                    .as("Expected Wishlist button to be visible")
                    .isTrue();
            soft.assertThat(productPage.isCompareButtonVisible())
                    .as("Expected Compare button to be visible")
                    .isTrue();
            soft.assertThat(productPage.isEmailAFriendVisible())
                    .as("Expected Email a Friend link to be visible")
                    .isTrue();
        });
    }

    // ── Add to Cart ───────────────────────────────────────────────────────────

    @Test(description = "TC-05 | Adding product to cart from product page adds it to cart")
    public void testAddToCartFromProductPage() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.addToCart();
        CartPage cartPage = homePage.goToCart();
        assertThat(cartPage.isCartEmpty())
                .as("Expected cart to have items after adding from product page")
                .isFalse();
    }

    @Test(description = "TC-06 | Adding product to cart shows 1 item in cart")
    public void testAddToCartCountFromProductPage() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.addToCart();
        CartPage cartPage = homePage.goToCart();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("Expected 1 item in cart after adding from product page")
                .isEqualTo(1);
    }

    // ── Wishlist ──────────────────────────────────────────────────────────────

    @Test(description = "TC-07 | Adding product to wishlist from product page adds it to wishlist")
    public void testAddToWishlistFromProductPage() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.addToWishlist();
        WishlistPage wishlistPage = homePage.goToWishlist();
        assertThat(wishlistPage.isWishlistEmpty())
                .as("Expected wishlist to have items after saving from product page")
                .isFalse();
    }

    @Test(description = "TC-08 | Added product appears in wishlist by name")
    public void testAddToWishlistProductNameFromProductPage() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.addToWishlist();
        WishlistPage wishlistPage = homePage.goToWishlist();
        assertThat(wishlistPage.isItemPresentInWishlist(PRODUCT_NAME))
                .as("Expected '" + PRODUCT_NAME + "' to be present in wishlist")
                .isTrue();
    }

    // ── Image Navigation ──────────────────────────────────────────────────────

    @Test(description = "TC-09 | Next image button is clickable on product page")
    public void testNextImageButton() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.nextImage();
        assertThat(productPage.isOnProductPage())
                .as("Expected to remain on product page after clicking next image")
                .isTrue();
    }

    @Test(description = "TC-10 | Previous image button is clickable on product page")
    public void testPreviousImageButton() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.nextImage().previousImage();
        assertThat(productPage.isOnProductPage())
                .as("Expected to remain on product page after clicking previous image")
                .isTrue();
    }

    // ── Customer Reviews ──────────────────────────────────────────────────────
    // Soft — all 4 review section elements are independent of each other

    @Test(description = "TC-11 | Customer Reviews section shows all expected elements")
    public void testCustomerReviewsSectionElements() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(productPage.isCustomerReviewsSectionVisible())
                    .as("Expected 'Customer Reviews' section to be visible")
                    .isTrue();
            soft.assertThat(productPage.isNoReviewsMessageVisible())
                    .as("Expected 'No Reviews Yet' message to be visible")
                    .isTrue();
            soft.assertThat(productPage.isShareYourExperienceVisible())
                    .as("Expected 'Share Your Experience' card to be visible")
                    .isTrue();
            soft.assertThat(productPage.isSignInToReviewButtonVisible())
                    .as("Expected 'Sign In to Review' button to be visible for unauthenticated users")
                    .isTrue();
        });
    }

    // ── Email a Friend ────────────────────────────────────────────────────────

    @Test(description = "TC-12 | Email a Friend dialog opens after clicking the link")
    public void testEmailAFriendDialogOpens() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.emailAFriend();
        assertThat(productPage.isEmailDialogVisible())
                .as("Expected 'Share with a Friend' dialog to be visible")
                .isTrue();
    }

    @Test(description = "TC-13 | Email a Friend form can be filled and submitted")
    public void testEmailAFriendFormSubmit() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        productPage.emailAFriend()
                .fillFriendEmail(FRIEND_EMAIL)
                .fillYourEmail(YOUR_EMAIL)
                .fillMessage(MESSAGE)
                .sendEmail();
        assertThat(productPage.isOnProductPage())
                .as("Expected to remain on product page after sending email")
                .isTrue();
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Test(description = "TC-14 | Back to Products returns to home page product list")
    public void testBackToProducts() {
        ProductPage productPage = homePage.clickProduct(PRODUCT_INDEX);
        HomePage returnedHome = productPage.backToProducts();
        assertThat(returnedHome.isOnHomePage())
                .as("Expected home page after Back to Products. URL: " + returnedHome.getCurrentUrl())
                .isTrue();
    }
}
