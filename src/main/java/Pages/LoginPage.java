package Pages;

import com.microsoft.playwright.Page;
import Utils.LogsUtil;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage extends BasePage {

    public LoginPage(Page page) {
        super(page, "LoginPage");
        LogsUtil.info("LoginPage initialized.");
    }

    /**************** Navigation ****************/

    public LoginPage navigateToLoginPage() {
        String url = jsonFile.getJsonData("url");
        page.navigate(url);
        LogsUtil.info("Navigated to Login Page: " + url);
        return this;
    }

    /**************** Actions ****************/

    public LoginPage enterEmail(String email) {
        locator.byPlaceholder(jsonFile.getJsonData("emailInput.placeholder"))
                .type(email, new com.microsoft.playwright.Locator.TypeOptions().setDelay(70));
        LogsUtil.info("Entered email: " + email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        locator.byId(jsonFile.getJsonData("passwordInput.id"))
                .type(password, new com.microsoft.playwright.Locator.TypeOptions().setDelay(70));
        LogsUtil.info("Entered password");
        return this;
    }

    public HomePage clickLogin() {
        locator.byRole(AriaRole.BUTTON, "Log In", true).click();
        LogsUtil.info("Clicked Login button.");
        return new HomePage(page);
    }

    public HomePage login(String email, String password) {
        LogsUtil.info("Performing login for: " + email);
        page.waitForTimeout(2000);
        return this.enterEmail(email)
                .enterPassword(password)
                .clickLogin();
    }

    /**************** State Queries ****************/

    public boolean isLoginSuccessful() {
        String css    = jsonFile.getJsonData("successMessage.css");
        boolean result = locator.isVisible(locator.byCss(css), 4000);
        if (result) LogsUtil.info("isLoginSuccessful: true");
        else        LogsUtil.error("isLoginSuccessful: false — toast not found");
        return result;
    }

    public boolean isLoginFailed() {
        String testId  = jsonFile.getJsonData("errorMessage.testId");
        boolean result = locator.isVisible(locator.byTestId(testId), 3000);
        if (result) LogsUtil.info("isLoginFailed: true — error message visible");
        else        LogsUtil.error("isLoginFailed: false — error message not found");
        return result;
    }

    public String getErrorMessage() {
        String testId = jsonFile.getJsonData("errorMessage.testId");
        String text   = locator.byTestId(testId).innerText().trim();
        if (!text.isEmpty()) LogsUtil.info("getErrorMessage: " + text);
        else                 LogsUtil.error("getErrorMessage: empty");
        return text;
    }

    public boolean isRedirectedToHome() {
        try {
            page.waitForURL(url -> !url.contains("/login"),
                    new Page.WaitForURLOptions().setTimeout(5000));
            LogsUtil.info("isRedirectedToHome: true — URL: " + page.url());
            return true;
        } catch (Exception e) {
            LogsUtil.error("isRedirectedToHome: false — still on: " + page.url());
            return false;
        }
    }

    public String getPasswordValidationMessage() {
        String message = (String) page.evaluate(
                "document.querySelector('#password').validationMessage");
        if (message != null && !message.isEmpty()) LogsUtil.info("Password validation: " + message);
        else                                        LogsUtil.error("No password validation message");
        return message != null ? message : "";
    }

    public boolean isPasswordValidationMessageDisplayed() {
        return !getPasswordValidationMessage().isEmpty();
    }

    public String getEmailValidationMessage() {
        String message = (String) page.evaluate(
                "document.querySelector('[placeholder=\"m@example.com\"]').validationMessage");
        if (message != null && !message.isEmpty()) LogsUtil.info("Email validation: " + message);
        else                                        LogsUtil.error("No email validation message");
        return message != null ? message : "";
    }

    public boolean isEmailValidationMessageDisplayed() {
        return !getEmailValidationMessage().isEmpty();
    }
}
