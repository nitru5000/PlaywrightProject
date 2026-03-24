package tests;

import Pages.CartPage;
import Pages.HomePage;
import Pages.WishlistPage;
import base.BaseTest;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WishlistPageTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        homePage = new HomePage(getPage());
        homePage.navigateToHomePage().acceptCookies();
    }

    // ── Page Structure ────────────────────────────────────────────────────────

    @Test(description = "TC-01 | Wishlist page URL contains /wishlist")
    public void testWishlistPageUrl() {
        WishlistPage wishlistPage = homePage.goToWishlist();
        assertThat(wishlistPage.isOnWishlistPage())
                .as("Expected URL to contain '/wishlist'. Got: " + wishlistPage.getCurrentUrl())
                .isTrue();
    }

    // ── Empty Wishlist State ──────────────────────────────────────────────────
    // Soft — empty message AND browse button are independent empty-state checks

    @Test(description = "TC-02 | Empty wishlist shows empty message and Browse Products button")
    public void testEmptyWishlistState() {
        WishlistPage wishlistPage = homePage.goToWishlist();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(wishlistPage.isWishlistEmpty())
                    .as("Expected empty wishlist message to be visible")
                    .isTrue();
            soft.assertThat(wishlistPage.isBrowseProductsButtonVisible())
                    .as("Expected Browse Products button to be visible on empty wishlist")
                    .isTrue();
        });
    }

    @Test(description = "TC-03 | Browse Products from empty wishlist returns to home page")
    public void testBrowseProductsNavigatesToHome() {
        WishlistPage wishlistPage = homePage.goToWishlist();
        HomePage returnedHome = wishlistPage.browseProducts();
        assertThat(returnedHome.isOnHomePage())
                .as("Expected home page after Browse Products. URL: " + returnedHome.getCurrentUrl())
                .isTrue();
    }

    // ── Save to Wishlist ──────────────────────────────────────────────────────

    @Test(description = "TC-04 | Saving a product makes wishlist non-empty")
    public void testSaveProductAddsToWishlist() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        assertThat(wishlistPage.isWishlistEmpty())
                .as("Expected wishlist to have items after saving a product")
                .isFalse();
    }

    @Test(description = "TC-05 | Wishlist title shows '1 items' after saving one product")
    public void testWishlistTitleAfterSavingOne() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        assertThat(wishlistPage.getWishlistTitle())
                .as("Expected wishlist title to contain '1 items'")
                .contains("1 items");
    }

    @Test(description = "TC-06 | Wishlist item count is 1 after saving one product")
    public void testWishlistCountAfterSavingOne() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        assertThat(wishlistPage.getWishlistItemCount())
                .as("Expected 1 item in wishlist after saving one product")
                .isEqualTo(1);
    }

    @Test(description = "TC-07 | Wishlist item count is 2 after saving two products")
    public void testWishlistCountAfterSavingTwo() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).addToWishlist(1).goToWishlist();
        assertThat(wishlistPage.getWishlistItemCount())
                .as("Expected 2 items in wishlist after saving two products")
                .isEqualTo(2);
    }

    @Test(description = "TC-08 | Saved product name appears correctly in wishlist")
    public void testSavedProductNameInWishlist() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        assertThat(wishlistPage.isItemPresentInWishlist(testData.getJsonData("products.index0.name")))
                .as("Expected product name to be present in wishlist")
                .isTrue();
    }

    @Test(description = "TC-09 | Saved product price shows $19.99 in wishlist")
    public void testSavedProductPriceInWishlist() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        assertThat(wishlistPage.getItemPrice(0))
                .as("Expected item price to be '$19.99' in wishlist")
                .isEqualTo(testData.getJsonData("products.index0.price"));
    }

    // ── Delete from Wishlist ──────────────────────────────────────────────────
    // Soft — empty AND browse button reappears are independent post-delete checks

    @Test(description = "TC-10 | Deleting the only item empties wishlist and shows Browse Products")
    public void testDeleteOnlyItemEmptiesWishlist() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        wishlistPage.deleteItemFromWishlist(0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(wishlistPage.isWishlistEmpty())
                    .as("Expected wishlist to be empty after deleting the only item")
                    .isTrue();
            soft.assertThat(wishlistPage.isBrowseProductsButtonVisible())
                    .as("Expected Browse Products button to reappear after wishlist becomes empty")
                    .isTrue();
        });
    }

    @Test(description = "TC-11 | Deleting one of two items leaves one item in wishlist")
    public void testDeleteOneOfTwoItemsLeavesOne() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).addToWishlist(1).goToWishlist();
        wishlistPage.deleteItemFromWishlist(0);
        assertThat(wishlistPage.getWishlistItemCount())
                .as("Expected 1 item remaining after deleting one of two")
                .isEqualTo(1);
    }

    // ── Add to Cart from Wishlist ─────────────────────────────────────────────
    // Soft — empty AND browse button reappears are independent post-add-to-cart checks

    @Test(description = "TC-12 | Adding wishlist item to cart removes it and shows Browse Products")
    public void testAddToCartRemovesItemFromWishlist() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        wishlistPage.addItemToCart(0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(wishlistPage.isWishlistEmpty())
                    .as("Expected wishlist to be empty after adding the only item to cart")
                    .isTrue();
            soft.assertThat(wishlistPage.isBrowseProductsButtonVisible())
                    .as("Expected Browse Products button to reappear after item moved to cart")
                    .isTrue();
        });
    }

    @Test(description = "TC-13 | Cart has 1 item after adding wishlist item to cart")
    public void testCartCountAfterAddToCartFromWishlist() {
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        wishlistPage.addItemToCart(0);
        CartPage cartPage = wishlistPage.goToCart();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("Expected 1 item in cart after adding from wishlist")
                .isEqualTo(1);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Test(description = "TC-14 | Clicking Home link from wishlist returns to home page")
    public void testHomeNavigationFromWishlist() {
        WishlistPage wishlistPage = homePage.goToWishlist();
        HomePage returnedHome = wishlistPage.backToHomePage();
        assertThat(returnedHome.isOnHomePage())
                .as("Expected home page after clicking Home from wishlist. URL: " + returnedHome.getCurrentUrl())
                .isTrue();
    }
}
