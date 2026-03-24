package tests;

import Pages.CartPage;
import Pages.HomePage;
import base.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartPageTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        homePage = new HomePage(getPage());
        homePage.navigateToHomePage().acceptCookies();
    }

    // ── Page Structure ────────────────────────────────────────────────────────
    // All cart page structure tests have one assertion each — hard is correct

    @Test(description = "TC-01 | Cart page displays when navigated to from home page")
    public void testCartPageDisplayed() {
        CartPage cartPage = homePage.goToCart();
        assertThat(cartPage.isCartPageDisplayed())
                .as("Expected cart page to be displayed")
                .isTrue();
    }

    @Test(description = "TC-02 | Empty cart shows empty cart message")
    public void testEmptyCartMessage() {
        CartPage cartPage = homePage.goToCart();
        assertThat(cartPage.isCartEmpty())
                .as("Expected empty cart message to be visible when no items added")
                .isTrue();
    }

    @Test(description = "TC-03 | Empty cart shows Start Shopping button")
    public void testEmptyCartStartShoppingButtonVisible() {
        CartPage cartPage = homePage.goToCart();
        assertThat(cartPage.isStartShoppingButtonVisible())
                .as("Expected Start Shopping button to be visible on empty cart")
                .isTrue();
    }

    // ── Add to Cart ───────────────────────────────────────────────────────────

    @Test(description = "TC-04 | Adding a product updates cart item count to 1")
    public void testAddOneProductToCart() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("Expected 1 item in cart after adding one product")
                .isEqualTo(1);
    }

    @Test(description = "TC-05 | Added product appears in cart by name")
    public void testAddedProductAppearsInCart() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.isItemPresentInCart(testData.getJsonData("products.index0.name")))
                .as("Expected '" + testData.getJsonData("products.index0.name") + "' to be present in cart")
                .isTrue();
    }

    @Test(description = "TC-06 | Cart is not empty after adding a product")
    public void testCartIsNotEmptyAfterAdding() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.isCartEmpty())
                .as("Expected cart to not be empty after adding a product")
                .isFalse();
    }

    @Test(description = "TC-07 | Adding two different products shows 2 items in cart")
    public void testAddTwoProductsToCart() {
        CartPage cartPage = homePage.addProductToCart(0).addProductToCart(1).goToCart();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("Expected 2 items in cart after adding two products")
                .isEqualTo(2);
    }

    // ── Quantity Controls ─────────────────────────────────────────────────────

    @Test(description = "TC-08 | Quantity controls are visible for cart item")
    public void testQuantityControlsVisible() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.hasQuantityControls(0))
                .as("Expected increase and decrease quantity buttons to be visible")
                .isTrue();
    }

    @Test(description = "TC-09 | Increasing quantity updates item quantity to 2")
    public void testIncreaseQuantity() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        cartPage.updateQuantity(0);
        assertThat(cartPage.getItemQuantity(0))
                .as("Expected quantity to be 2 after one increase")
                .isEqualTo(2);
    }

    @Test(description = "TC-10 | Increasing quantity twice updates item quantity to 3")
    public void testIncreaseQuantityTwice() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        cartPage.updateQuantity(0).updateQuantity(0);
        assertThat(cartPage.getItemQuantity(0))
                .as("Expected quantity to be 3 after two increases")
                .isEqualTo(3);
    }

    @Test(description = "TC-11 | Decreasing quantity from 2 returns item quantity to 1")
    public void testDecreaseQuantity() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        cartPage.updateQuantity(0).decreaseQuantity(0);
        assertThat(cartPage.getItemQuantity(0))
                .as("Expected quantity to be 1 after increase then decrease")
                .isEqualTo(1);
    }

    // ── Total Price ───────────────────────────────────────────────────────────

    @Test(description = "TC-12 | Displayed total matches calculated subtotal")
    public void testTotalMatchesSubtotal() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.verifyTotalMatchesSubtotal())
                .as("Expected displayed total to match calculated subtotal")
                .isTrue();
    }

    @Test(description = "TC-13 | Total price increases after quantity increase")
    public void testTotalIncreasesAfterQuantityIncrease() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        double before = Double.parseDouble(cartPage.getTotalPrice().replaceAll("[^0-9.]", ""));
        cartPage.updateQuantity(0);
        double after = Double.parseDouble(cartPage.getTotalPrice().replaceAll("[^0-9.]", ""));

        assertThat(after)
                .as("Expected total to increase after quantity increase. Before: " + before)
                .isGreaterThan(before);
    }

    @Test(description = "TC-14 | Total price decreases after quantity decrease")
    public void testTotalDecreasesAfterQuantityDecrease() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        cartPage.updateQuantity(0); // qty = 2
        double before = Double.parseDouble(cartPage.getTotalPrice().replaceAll("[^0-9.]", ""));
        cartPage.decreaseQuantity(0); // qty = 1
        double after = Double.parseDouble(cartPage.getTotalPrice().replaceAll("[^0-9.]", ""));

        assertThat(after)
                .as("Expected total to decrease after quantity decrease. Before: " + before)
                .isLessThan(before);
    }

    // ── Remove Item ───────────────────────────────────────────────────────────

    @Test(description = "TC-15 | Remove button is visible for cart item")
    public void testRemoveButtonVisible() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.hasRemoveButton(0))
                .as("Expected remove button to be visible for item 0")
                .isTrue();
    }

    @Test(description = "TC-16 | Removing the only item empties the cart")
    public void testRemoveOnlyItemEmptiesCart() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        cartPage.removeItem(0);
        assertThat(cartPage.isCartEmpty())
                .as("Expected cart to be empty after removing the only item")
                .isTrue();
    }

    @Test(description = "TC-17 | Removing one of two items leaves one item in cart")
    public void testRemoveOneOfTwoItems() {
        CartPage cartPage = homePage.addProductToCart(0).addProductToCart(1).goToCart();
        cartPage.removeItem(0);
        assertThat(cartPage.getDisplayedItemsCount())
                .as("Expected 1 item remaining after removing one of two")
                .isEqualTo(1);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Test(description = "TC-18 | Continue Shopping returns to home page")
    public void testContinueShoppingNavigatesToHome() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        HomePage returnedHome = cartPage.continueShopping();
        assertThat(returnedHome.isOnHomePage())
                .as("Expected to be on home page after Continue Shopping. URL: " + returnedHome.getCurrentUrl())
                .isTrue();
    }

    @Test(description = "TC-19 | Start Shopping from empty cart returns to home page")
    public void testStartShoppingNavigatesToHome() {
        CartPage cartPage = homePage.goToCart();
        HomePage returnedHome = cartPage.startShopping();
        assertThat(returnedHome.isOnHomePage())
                .as("Expected to be on home page after Start Shopping. URL: " + returnedHome.getCurrentUrl())
                .isTrue();
    }

    @Test(description = "TC-20 | Proceed to Checkout button is enabled when cart has items")
    public void testCheckoutButtonEnabledWithItems() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.isCheckoutButtonEnabled())
                .as("Expected Proceed to Checkout button to be enabled when cart has items")
                .isTrue();
    }

    @Test(description = "TC-21 | Continue Shopping button is visible when cart has items")
    public void testContinueShoppingButtonVisible() {
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.isContinueShoppingButtonVisible())
                .as("Expected Continue Shopping button to be visible")
                .isTrue();
    }
}
