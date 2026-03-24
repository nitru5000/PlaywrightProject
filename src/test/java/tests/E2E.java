package tests;

import Pages.*;
import base.BaseTest;
import Utils.LogsUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class E2E extends BaseTest {

    private HomePage homePage;

    private static final String FIRST_NAME = testData.getJsonData("shipping.firstName");
    private static final String LAST_NAME  = testData.getJsonData("shipping.lastName");
    private static final String ADDRESS    = testData.getJsonData("shipping.address");
    private static final String CITY       = testData.getJsonData("shipping.city");
    private static final String STATE      = testData.getJsonData("shipping.state");
    private static final String ZIP        = testData.getJsonData("shipping.zipCode");
    private static final String PHONE      = testData.getJsonData("shipping.phone");

    private static final String CARD_NUMBER = testData.getJsonData("payment.cardNumber");
    private static final String HOLDER_NAME = testData.getJsonData("payment.holderName");
    private static final String EXPIRY_DATE = testData.getJsonData("payment.expiryDate");
    private static final String CVV         = testData.getJsonData("payment.cvv");

    private static final String VALID_EMAIL    = testData.getJsonData("users.valid.email");
    private static final String VALID_PASSWORD = testData.getJsonData("users.valid.password");
    private static final String DISCOUNT_SAVE10 = testData.getJsonData("discountCodes.save10");

    @BeforeMethod(alwaysRun = true)
    public void setUpE2E() {
        homePage = new HomePage(getPage());
        homePage.navigateToHomePage()
                .acceptCookies()
                .clickHeaderLogin()
                .login(VALID_EMAIL, VALID_PASSWORD)
                .goToHome();

        // Hard — all E2E tests require login; if this fails the test must stop
        assertThat(homePage.isLoggedIn())
                .as("setUp Failed: Expected user to be logged in before test starts")
                .isTrue();
    }

    // ── E2E-01: Browse Home Page → Cart → Checkout ───────────────────────────

    @Test(description = "E2E-01 | User adds product from home page and completes checkout")
    public void testBrowseToCheckout() {
        LogsUtil.info("E2E-01 START: Browse → cart → checkout");

        // Step 1 — Hard: cart must have items before proceeding
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.isCartEmpty())
                .as("E2E-01 Step 1: Expected cart to have items")
                .isFalse();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("E2E-01 Step 1: Expected 1 item in cart")
                .isEqualTo(1);

        // Step 2 — Hard: must be on checkout before filling forms
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        assertThat(checkoutPage.getCurrentUrl())
                .as("E2E-01 Step 2: Expected checkout URL")
                .contains("/checkout");
        assertThat(checkoutPage.isShippingStepActive())
                .as("E2E-01 Step 2: Expected shipping step to be active")
                .isTrue();

        // Step 3 — Hard: must advance to payment before filling payment info
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping()
                .continueToPayment();
        assertThat(checkoutPage.isPaymentStepActive())
                .as("E2E-01 Step 3: Expected payment step to be active")
                .isTrue();

        // Step 4 — Hard: order confirmation depends on payment being submitted
        checkoutPage.fillPaymentInfo(CARD_NUMBER, HOLDER_NAME, EXPIRY_DATE, CVV).payNow();
        assertThat(checkoutPage.isOrderConfirmed())
                .as("E2E-01 Step 4: Expected 'Payment Successful!'")
                .isTrue();
        assertThat(checkoutPage.getOrderNumber())
                .as("E2E-01 Step 4: Expected order number to start with '#'")
                .startsWith("#");
        assertThat(checkoutPage.isViewOrdersButtonVisible())
                .as("E2E-01 Step 4: Expected 'View Orders' button to be visible")
                .isTrue();

        LogsUtil.info("E2E-01 PASSED");
    }

    // ── E2E-02: Product Page → Cart → Checkout ───────────────────────────────

    @Test(description = "E2E-02 | User opens product detail page and completes checkout")
    public void testProductPageToCheckout() {
        LogsUtil.info("E2E-02 START: Product page → cart → checkout");

        // Step 1 — Hard: product must be in stock before adding to cart
        ProductPage productPage = homePage.clickProduct(1);
        assertThat(productPage.isOnProductPage())
                .as("E2E-02 Step 1: Expected product detail page")
                .isTrue();
        assertThat(productPage.isInStock())
                .as("E2E-02 Step 1: Expected product to be In Stock")
                .isTrue();

        // Step 2 — Hard: cart must have item before proceeding
        productPage.addToCart();
        CartPage cartPage = homePage.goToCart();
        assertThat(cartPage.isCartEmpty())
                .as("E2E-02 Step 2: Expected cart to have items")
                .isFalse();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("E2E-02 Step 2: Expected 1 item in cart")
                .isEqualTo(1);

        // Step 3 — Hard: order confirmed depends on full checkout flow
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping().continueToPayment()
                .fillPaymentInfo(CARD_NUMBER, HOLDER_NAME, EXPIRY_DATE, CVV).payNow();
        assertThat(checkoutPage.isOrderConfirmed())
                .as("E2E-02 Step 3: Expected order confirmation")
                .isTrue();

        LogsUtil.info("E2E-02 PASSED");
    }

    // ── E2E-03: Wishlist → Cart → Checkout ───────────────────────────────────

    @Test(description = "E2E-03 | User saves product to wishlist then moves it to cart and checks out")
    public void testWishlistToCartToCheckout() {
        LogsUtil.info("E2E-03 START: Wishlist → cart → checkout");

        // Step 1 — Hard: wishlist must have item before moving to cart
        WishlistPage wishlistPage = homePage.addToWishlist(0).goToWishlist();
        assertThat(wishlistPage.isWishlistEmpty())
                .as("E2E-03 Step 1: Expected wishlist to have items")
                .isFalse();
        assertThat(wishlistPage.getWishlistItemCount())
                .as("E2E-03 Step 1: Expected 1 item in wishlist")
                .isEqualTo(1);

        // Step 2 — Hard: wishlist must be empty before checking cart
        wishlistPage.addItemToCart(0);
        assertThat(wishlistPage.isWishlistEmpty())
                .as("E2E-03 Step 2: Expected wishlist to be empty after moving to cart")
                .isTrue();

        // Step 3 — Hard: cart must have item before checkout
        CartPage cartPage = wishlistPage.goToCart();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("E2E-03 Step 3: Expected 1 item in cart")
                .isEqualTo(1);

        // Step 4 — Hard: order depends on full checkout flow
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping().continueToPayment()
                .fillPaymentInfo(CARD_NUMBER, HOLDER_NAME, EXPIRY_DATE, CVV).payNow();
        assertThat(checkoutPage.isOrderConfirmed())
                .as("E2E-03 Step 4: Expected order confirmation")
                .isTrue();

        LogsUtil.info("E2E-03 PASSED");
    }

    // ── E2E-04: Filter → Cart → Discount → Checkout ──────────────────────────

    @Test(description = "E2E-04 | User filters products, adds to cart, applies discount and checks out")
    public void testFilterToDiscountToCheckout() {
        LogsUtil.info("E2E-04 START: Filter → cart → discount → checkout");

        // Step 1 — Hard: filtered products must exist before adding to cart
        int totalCount = homePage.getProductCount();
        homePage.selectCategoryFilter(2);
        assertThat(homePage.getProductCount())
                .as("E2E-04 Step 1: Expected electronics products to be displayed")
                .isGreaterThan(0)
                .isLessThanOrEqualTo(totalCount);

        // Step 2 — Hard: cart must have item before checkout
        CartPage cartPage = homePage.addProductToCart(0).goToCart();
        assertThat(cartPage.isCartEmpty())
                .as("E2E-04 Step 2: Expected cart to have items")
                .isFalse();

        // Step 3 — Hard: coupon must be applied before comparing totals
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        double before = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));
        checkoutPage.applyCouponCode(DISCOUNT_SAVE10);
        assertThat(checkoutPage.isCouponApplied())
                .as("E2E-04 Step 3: Expected SAVE10 to be applied")
                .isTrue();
        double after = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));
        assertThat(after)
                .as("E2E-04 Step 3: Expected total to decrease after SAVE10")
                .isLessThan(before);

        // Step 4 — Hard: depends on all previous steps
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping().continueToPayment()
                .fillPaymentInfo(CARD_NUMBER, HOLDER_NAME, EXPIRY_DATE, CVV).payNow();
        assertThat(checkoutPage.isOrderConfirmed())
                .as("E2E-04 Step 4: Expected order confirmation")
                .isTrue();

        LogsUtil.info("E2E-04 PASSED");
    }

    // ── E2E-05: Multi-item Cart → Checkout ───────────────────────────────────

    @Test(description = "E2E-05 | User adds multiple products and completes checkout")
    public void testMultiItemCartToCheckout() {
        LogsUtil.info("E2E-05 START: Multi-item cart → checkout");

        // Step 1 — Hard: cart state must be verified before quantity update
        CartPage cartPage = homePage.addProductToCart(0).addProductToCart(1).goToCart();
        assertThat(cartPage.getDisplayedItemsCount())
                .as("E2E-05 Step 1: Expected 2 items in cart")
                .isEqualTo(2);
        assertThat(cartPage.verifyTotalMatchesSubtotal())
                .as("E2E-05 Step 1: Expected total to match subtotal")
                .isTrue();

        // Step 2 — Hard: quantity must be verified before proceeding
        cartPage.updateQuantity(0);
        assertThat(cartPage.getItemQuantity(0))
                .as("E2E-05 Step 2: Expected quantity 2 after increase")
                .isEqualTo(2);

        // Step 3 — Hard: depends on all previous steps
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping().continueToPayment()
                .fillPaymentInfo(CARD_NUMBER, HOLDER_NAME, EXPIRY_DATE, CVV).payNow();
        assertThat(checkoutPage.isOrderConfirmed())
                .as("E2E-05 Step 3: Expected 'Payment Successful!'")
                .isTrue();
        assertThat(checkoutPage.getOrderNumber())
                .as("E2E-05 Step 3: Expected order number to start with '#'")
                .startsWith("#");

        LogsUtil.info("E2E-05 PASSED");
    }

    // ── E2E-06: Continue Shopping after Checkout ─────────────────────────────

    @Test(description = "E2E-06 | User completes checkout then continues shopping on home page")
    public void testCheckoutThenContinueShopping() {
        LogsUtil.info("E2E-06 START: Checkout → continue shopping");

        // Step 1 — Hard: must confirm order before continuing shopping
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping().continueToPayment()
                .fillPaymentInfo(CARD_NUMBER, HOLDER_NAME, EXPIRY_DATE, CVV).payNow();
        assertThat(checkoutPage.isOrderConfirmed())
                .as("E2E-06 Step 1: Expected order confirmation")
                .isTrue();

        // Step 2 — Hard: page and grid depend on successful navigation
        HomePage returnedHome = checkoutPage.continueToShopping();
        assertThat(returnedHome.isOnHomePage())
                .as("E2E-06 Step 2: Expected to be on home page after Continue Shopping")
                .isTrue();
        assertThat(returnedHome.isProductGridVisible())
                .as("E2E-06 Step 2: Expected product grid to be visible")
                .isTrue();

        LogsUtil.info("E2E-06 PASSED");
    }
}
