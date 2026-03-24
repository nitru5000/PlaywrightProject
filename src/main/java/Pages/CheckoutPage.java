package Pages;

import com.microsoft.playwright.Page;
import Utils.LogsUtil;
import com.microsoft.playwright.options.AriaRole;

public class CheckoutPage extends BasePage {

    public CheckoutPage(Page page) {
        super(page, "CheckoutPage");
        LogsUtil.info("CheckoutPage initialized.");
    }

    /**************** Navigation ****************/

    public CheckoutPage navigateToCheckout() {
        page.navigate(jsonFile.getJsonData("url"));
        LogsUtil.info("Navigated to Checkout Page: " + jsonFile.getJsonData("url"));
        return this;
    }

    /**************** Shipping Information ****************/

    public CheckoutPage enterFirstName(String firstName) {
        locator.byId(jsonFile.getJsonData("shippingInfo.firstName"))
                .type(firstName, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered first name: " + firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        locator.byId(jsonFile.getJsonData("shippingInfo.lastName"))
                .type(lastName, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered last name: " + lastName);
        return this;
    }

    public CheckoutPage enterAddress(String address) {
        locator.byId(jsonFile.getJsonData("shippingInfo.address"))
                .type(address, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered address: " + address);
        return this;
    }

    public CheckoutPage enterCity(String city) {
        locator.byId(jsonFile.getJsonData("shippingInfo.city"))
                .type(city, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered city: " + city);
        return this;
    }

    public CheckoutPage enterState(String state) {
        locator.byId(jsonFile.getJsonData("shippingInfo.state"))
                .type(state, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered state: " + state);
        return this;
    }

    public CheckoutPage enterZipCode(String zipCode) {
        locator.byId(jsonFile.getJsonData("shippingInfo.zipCode"))
                .type(zipCode, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered ZIP code: " + zipCode);
        return this;
    }

    public CheckoutPage enterPhone(String phone) {
        locator.byId(jsonFile.getJsonData("shippingInfo.phone"))
                .type(phone, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered phone: " + phone);
        return this;
    }

    public CheckoutPage fillShippingInfo(String firstName, String lastName,
                                         String address, String city, String state,
                                         String zipCode, String phone) {
        return enterFirstName(firstName)
                .enterLastName(lastName)
                .enterAddress(address)
                .enterCity(city)
                .enterState(state)
                .enterZipCode(zipCode)
                .enterPhone(phone);
    }

    /**************** Shipping Method ****************/

    public CheckoutPage selectStandardShipping() {
        locator.byRole(AriaRole.RADIO, jsonFile.getJsonData("shipping.standard"), false).click();
        LogsUtil.info("Selected Standard Shipping");
        return this;
    }

    public CheckoutPage selectExpressShipping() {
        locator.byRole(AriaRole.RADIO, jsonFile.getJsonData("shipping.express"), false).click();
        LogsUtil.info("Selected Express Shipping");
        return this;
    }

    public CheckoutPage selectOvernightShipping() {
        locator.byRole(AriaRole.RADIO, jsonFile.getJsonData("shipping.overnight"), false).click();
        LogsUtil.info("Selected Overnight Shipping");
        return this;
    }

    /**************** Step Navigation ****************/

    public CheckoutPage continueToPayment() {
        locator.byText(jsonFile.getJsonData("buttons.continueToPaymentText")).click();
        LogsUtil.info("Clicked Continue to Payment");
        return this;
    }

    /**************** Discount ****************/

    public CheckoutPage applyCouponCode(String discountCode) {
        locator.byPlaceholder(jsonFile.getJsonData("discount.inputPlaceholder"))
                .type(discountCode, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        locator.byText(jsonFile.getJsonData("discount.applyText")).click();
        LogsUtil.info("Applied coupon code: " + discountCode);
        locator.byTestId(jsonFile.getJsonData("discount.removeCoupon")).waitFor();
        LogsUtil.info("Coupon applied and confirmed: " + discountCode);
        return this;
    }

    public CheckoutPage removeCouponCode() {
        locator.byTestId(jsonFile.getJsonData("discount.removeCoupon")).click();
        LogsUtil.info("Removed coupon code");
        locator.byPlaceholder(jsonFile.getJsonData("discount.inputPlaceholder")).waitFor();
        LogsUtil.info("Coupon removed — input field restored");
        return this;
    }

    /**************** Payment Information ****************/

    public CheckoutPage enterCardNumber(String cardNumber) {
        locator.byTestId(jsonFile.getJsonData("Card_inputs.cardNumber")).first()
                .type(cardNumber, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered card number");
        return this;
    }

    public CheckoutPage enterCardHolderName(String cardHolderName) {
        locator.byTestId(jsonFile.getJsonData("Card_inputs.holderName")).first()
                .type(cardHolderName, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered card holder name: " + cardHolderName);
        return this;
    }

    public CheckoutPage enterExpirationDate(String expirationDate) {
        locator.byTestId(jsonFile.getJsonData("Card_inputs.expirydate")).first()
                .type(expirationDate, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered expiration date: " + expirationDate);
        return this;
    }

    public CheckoutPage enterCVV(String cvv) {
        locator.byTestId(jsonFile.getJsonData("Card_inputs.cvv")).first()
                .type(cvv, new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Entered CVV");
        return this;
    }

    public CheckoutPage fillPaymentInfo(String cardNumber, String holderName,
                                        String expiryDate, String cvv) {
        return enterCardNumber(cardNumber)
                .enterCardHolderName(holderName)
                .enterExpirationDate(expiryDate)
                .enterCVV(cvv);
    }

    /**************** Actions ****************/

    public CheckoutPage payNow() {
        locator.byText(jsonFile.getJsonData("buttons.payNowText")).click();
        LogsUtil.info("Clicked Pay Now");
        page.waitForTimeout(3000);
        return this;
    }

    public HomePage continueToShopping() {
        locator.byText(jsonFile.getJsonData("buttons.continueShoppingText")).click();
        LogsUtil.info("Clicked Continue Shopping");
        page.waitForTimeout(2000);
        return new HomePage(page);
    }

    public CheckoutPage viewOrders() {
        locator.byText(jsonFile.getJsonData("buttons.viewOrdersText")).click();
        LogsUtil.info("Clicked View Orders");
        return this;
    }

    /**************** State Queries ****************/

    public boolean isShippingStepActive() {
        boolean result = locator.byTestId(jsonFile.getJsonData("checkoutSteps.shipping.testId")).isVisible();
        if (result) LogsUtil.info("isShippingStepActive: true");
        else        LogsUtil.error("isShippingStepActive: false");
        return result;
    }

    public boolean isPaymentStepActive() {
        boolean result = locator.byTestId(jsonFile.getJsonData("checkoutSteps.payment.testId")).isVisible();
        if (result) LogsUtil.info("isPaymentStepActive: true");
        else        LogsUtil.error("isPaymentStepActive: false");
        return result;
    }

    public boolean isCouponApplied() {
        boolean result = locator.byTestId(jsonFile.getJsonData("discount.removeCoupon")).isVisible();
        if (result) LogsUtil.info("isCouponApplied: true");
        else        LogsUtil.error("isCouponApplied: false");
        return result;
    }

    public boolean isOrderConfirmed() {
        // Confirmed from screenshot: heading is "Payment Successful!"
        boolean result = locator.byText("Payment Successful!").isVisible();
        if (result) LogsUtil.info("isOrderConfirmed: true");
        else        LogsUtil.error("isOrderConfirmed: false");
        return result;
    }

    public String getOrderNumber() {
        // Confirmed from screenshot: "Order Number: #810016"
        String fullText   = locator.locator("text=/Order Number:/").textContent().trim();
        String orderNumber = fullText.replace("Order Number:", "").trim();
        LogsUtil.info("getOrderNumber: " + orderNumber);
        return orderNumber;
    }

    public boolean isViewOrdersButtonVisible() {
        boolean result = locator.byText("View Orders").isVisible();
        if (result) LogsUtil.info("isViewOrdersButtonVisible: true");
        else        LogsUtil.error("isViewOrdersButtonVisible: false");
        return result;
    }

    public boolean isOrderSummaryVisible() {
        boolean result = locator.byTestId(jsonFile.getJsonData("orderSummary.container")).isVisible();
        if (result) LogsUtil.info("isOrderSummaryVisible: true");
        else        LogsUtil.error("isOrderSummaryVisible: false");
        return result;
    }

    public boolean isContinueToPaymentButtonVisible() {
        boolean result = locator.byTestId(jsonFile.getJsonData("buttons.continueToPayment.testId")).isVisible();
        if (result) LogsUtil.info("isContinueToPaymentButtonVisible: true");
        else        LogsUtil.error("isContinueToPaymentButtonVisible: false");
        return result;
    }

    public String getOrderSummaryTotal() {
        String total = locator.byTestId(jsonFile.getJsonData("orderSummary.total")).textContent().trim();
        LogsUtil.info("Order summary total: " + total);
        return total;
    }

    public String getOrderSummarySubtotal() {
        String subtotal = locator.byTestId(jsonFile.getJsonData("orderSummary.subtotal")).textContent().trim();
        LogsUtil.info("Order summary subtotal: " + subtotal);
        return subtotal;
    }

    public String getOrderSummaryShipping() {
        String shipping = locator.byTestId(jsonFile.getJsonData("orderSummary.shipping")).textContent().trim();
        LogsUtil.info("Order summary shipping: " + shipping);
        return shipping;
    }

    public String getOrderSummaryTax() {
        String tax = locator.byTestId(jsonFile.getJsonData("orderSummary.tax")).textContent().trim();
        LogsUtil.info("Order summary tax: " + tax);
        return tax;
    }

    public String getOrderSummaryDiscount() {
        String discount = locator.byTestId(jsonFile.getJsonData("orderSummary.discount")).textContent().trim();
        LogsUtil.info("Order summary discount: " + discount);
        return discount;
    }
}
