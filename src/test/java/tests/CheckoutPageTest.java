package tests;

import Pages.CheckoutPage;
import Pages.HomePage;
import base.BaseTest;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutPageTest extends BaseTest {

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

    private static final String DISCOUNT_SAVE10   = testData.getJsonData("discountCodes.save10");
    private static final String DISCOUNT_SAVE20   = testData.getJsonData("discountCodes.save20");
    private static final String DISCOUNT_FREESHIP = testData.getJsonData("discountCodes.freeShip");

    private static final String STANDARD_SHIPPING_COST  = testData.getJsonData("shippingCosts.standard");
    private static final String EXPRESS_SHIPPING_COST   = testData.getJsonData("shippingCosts.express");
    private static final String OVERNIGHT_SHIPPING_COST = testData.getJsonData("shippingCosts.overnight");

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        homePage = new HomePage(getPage());
        homePage.navigateToHomePage().acceptCookies();
    }

    // ── Page Structure ────────────────────────────────────────────────────────

    @Test(description = "TC-01 | Checkout page URL contains /checkout")
    public void testCheckoutPageUrl() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        assertThat(checkoutPage.getCurrentUrl())
                .as("Expected URL to contain '/checkout'")
                .contains("/checkout");
    }

    @Test(description = "TC-02 | Shipping step (step 1) is active on checkout page load")
    public void testShippingStepActiveOnLoad() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        assertThat(checkoutPage.isShippingStepActive())
                .as("Expected shipping step to be active on checkout page load")
                .isTrue();
    }

    @Test(description = "TC-03 | Order summary is visible on checkout page load")
    public void testOrderSummaryVisible() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        assertThat(checkoutPage.isOrderSummaryVisible())
                .as("Expected order summary panel to be visible")
                .isTrue();
    }

    // ── Order Summary Values ──────────────────────────────────────────────────
    // Soft — all 4 values are completely independent of each other.
    // If subtotal is wrong, we still want to know if tax and total are also wrong.

    @Test(description = "TC-04 | Order summary shows correct subtotal, shipping, tax and total")
    public void testOrderSummaryValues() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(checkoutPage.getOrderSummarySubtotal())
                    .as("Order summary subtotal")
                    .isEqualTo(testData.getJsonData("orderSummary.subtotal"));
            soft.assertThat(checkoutPage.getOrderSummaryShipping())
                    .as("Order summary shipping (default Standard)")
                    .isEqualTo(testData.getJsonData("orderSummary.shipping"));
            soft.assertThat(checkoutPage.getOrderSummaryTax())
                    .as("Order summary tax (10% of subtotal)")
                    .isEqualTo(testData.getJsonData("orderSummary.tax"));
            soft.assertThat(checkoutPage.getOrderSummaryTotal())
                    .as("Order summary total")
                    .isEqualTo(testData.getJsonData("orderSummary.total"));
        });
    }

    // ── Shipping Method ───────────────────────────────────────────────────────

    @Test(description = "TC-05 | Selecting Express Shipping updates shipping cost to $12.99")
    public void testExpressShippingCost() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        checkoutPage.selectExpressShipping();
        assertThat(checkoutPage.getOrderSummaryShipping())
                .as("Expected Express shipping cost")
                .isEqualTo(EXPRESS_SHIPPING_COST);
    }

    @Test(description = "TC-06 | Selecting Overnight Shipping updates shipping cost to $24.99")
    public void testOvernightShippingCost() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        checkoutPage.selectOvernightShipping();
        assertThat(checkoutPage.getOrderSummaryShipping())
                .as("Expected Overnight shipping cost")
                .isEqualTo(OVERNIGHT_SHIPPING_COST);
    }

    @Test(description = "TC-07 | Selecting Standard Shipping after Express returns cost to $5.99")
    public void testStandardShippingCostAfterSwitch() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        checkoutPage.selectExpressShipping().selectStandardShipping();
        assertThat(checkoutPage.getOrderSummaryShipping())
                .as("Expected shipping to revert to Standard cost after switching back")
                .isEqualTo(STANDARD_SHIPPING_COST);
    }

    @Test(description = "TC-08 | Switching from Standard to Express increases order total")
    public void testExpressShippingIncreasesTotal() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        double before = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));
        checkoutPage.selectExpressShipping();
        double after = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));

        assertThat(after)
                .as("Expected total to increase after switching to Express Shipping")
                .isGreaterThan(before);
    }

    // ── Shipping Information → Advance to Payment ─────────────────────────────

    @Test(description = "TC-09 | Filling all shipping fields and clicking Continue advances to payment step")
    public void testFillShippingInfoAdvancesToPayment() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping()
                .continueToPayment();
        assertThat(checkoutPage.isPaymentStepActive())
                .as("Expected payment step to be active after completing shipping info")
                .isTrue();
    }

    @Test(description = "TC-10 | After advancing to payment, shipping step is no longer active")
    public void testShippingStepInactiveAfterAdvancing() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        checkoutPage.fillShippingInfo(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE)
                .selectStandardShipping()
                .continueToPayment();
        assertThat(checkoutPage.isShippingStepActive())
                .as("Expected shipping step to become inactive after advancing to payment")
                .isFalse();
    }

    // ── Discount Code ─────────────────────────────────────────────────────────

    @Test(description = "TC-11 | Applying SAVE10 applies coupon and reduces order total")
    public void testApplySave10ReducesTotal() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        double before = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));
        checkoutPage.applyCouponCode(DISCOUNT_SAVE10);
        double after = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));

        // Soft — coupon applied AND total decreased are independent verifications
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(checkoutPage.isCouponApplied())
                    .as("Expected SAVE10 coupon to be applied (remove button visible)")
                    .isTrue();
            soft.assertThat(after)
                    .as("Expected total to decrease after SAVE10. Before: " + before)
                    .isLessThan(before);
        });
    }

    @Test(description = "TC-12 | Applying SAVE20 reduces order total more than SAVE10")
    public void testApplySave20ReducesTotalMoreThanSave10() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();

        // Hard — must confirm SAVE10 applied before switching to SAVE20
        checkoutPage.applyCouponCode(DISCOUNT_SAVE10);
        assertThat(checkoutPage.isCouponApplied())
                .as("Expected SAVE10 to be applied first")
                .isTrue();

        double totalWithSave10 = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));
        checkoutPage.removeCouponCode().applyCouponCode(DISCOUNT_SAVE20);

        assertThat(checkoutPage.isCouponApplied())
                .as("Expected SAVE20 to be applied")
                .isTrue();

        double totalWithSave20 = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));
        assertThat(totalWithSave20)
                .as("Expected SAVE20 total (" + totalWithSave20 + ") to be less than SAVE10 total (" + totalWithSave10 + ")")
                .isLessThan(totalWithSave10);
    }

    @Test(description = "TC-13 | Applying FREESHIP applies coupon and reduces order total")
    public void testApplyFreeShipReducesTotal() {
        CheckoutPage checkoutPage = homePage.addProductToCart(0).goToCart().proceedToCheckout();
        double before = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));
        checkoutPage.applyCouponCode(DISCOUNT_FREESHIP);
        double after = Double.parseDouble(checkoutPage.getOrderSummaryTotal().replaceAll("[^0-9.]", ""));

        // Soft — coupon applied AND total decreased are independent verifications
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(checkoutPage.isCouponApplied())
                    .as("Expected FREESHIP coupon to be applied")
                    .isTrue();
            soft.assertThat(after)
                    .as("Expected total to decrease after FREESHIP. Before: " + before)
                    .isLessThan(before);
        });
    }

    @Test(description = "TC-14 | Applying SAVE10 shows discount line in order summary")
    public void testDiscountLineAppearsAfterCode() {
        CheckoutPage checkoutPage = homePage.addProductToCart(3).goToCart().proceedToCheckout();
        checkoutPage.applyCouponCode(DISCOUNT_SAVE10);

        // Soft — coupon applied AND discount line visible are independent verifications
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(checkoutPage.isCouponApplied())
                    .as("Expected SAVE10 coupon to be applied")
                    .isTrue();
            soft.assertThat(checkoutPage.getOrderSummaryDiscount())
                    .as("Expected discount amount to appear in order summary")
                    .isNotEmpty();
        });
    }
}
