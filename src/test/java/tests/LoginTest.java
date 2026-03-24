package tests;

import Pages.HomePage;
import Pages.LoginPage;
import base.BaseTest;
import Utils.LogsUtil;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {

    private static final String VALID_EMAIL      = testData.getJsonData("users.valid.email");
    private static final String VALID_PASSWORD   = testData.getJsonData("users.valid.password");
    private static final String INVALID_EMAIL    = testData.getJsonData("users.invalid.email");
    private static final String INVALID_PASSWORD = testData.getJsonData("users.invalid.password");

    private LoginPage openLoginPage() {
        return new HomePage(getPage())
                .navigateToHomePage()
                .acceptCookies()
                .clickHeaderLogin();
    }

    @Test(description = "TC-01 | Valid credentials — redirected to home page")
    public void testValidLogin() {
        LogsUtil.info("TC-01 START: testValidLogin");

        LoginPage loginPage = openLoginPage();
        loginPage.enterEmail(VALID_EMAIL)
                 .enterPassword(VALID_PASSWORD)
                 .clickLogin();

        // Hard — single check, nothing depends on it
        assertThat(loginPage.isLoginSuccessful())
                .as("Expected success toast to be displayed after valid login")
                .isTrue();

        LogsUtil.info("TC-01 PASSED");
    }

    @Test(description = "TC-02 | Invalid credentials — error message appears")
    public void testInvalidLogin() {
        LogsUtil.info("TC-02 START: testInvalidLogin");

        LoginPage loginPage = openLoginPage();
        loginPage.enterEmail(INVALID_EMAIL)
                 .enterPassword(INVALID_PASSWORD)
                 .clickLogin();

        // Soft — error visible AND error text are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(loginPage.isLoginFailed())
                    .as("Expected error message to be visible")
                    .isTrue();
            soft.assertThat(loginPage.getErrorMessage())
                    .as("Expected specific error text")
                    .isEqualTo("Invalid email or password");
        });

        LogsUtil.info("TC-02 PASSED");
    }

    @Test(description = "TC-03 | Empty email and password — browser validation appears")
    public void testEmptyEmailAndPassword() {
        LoginPage loginPage = openLoginPage();
        loginPage.enterEmail("")
                 .enterPassword("")
                 .clickLogin();

        // Hard — single check
        assertThat(loginPage.isEmailValidationMessageDisplayed())
                .as("Expected email validation message to appear for empty fields")
                .isTrue();
    }

    @Test(description = "TC-04 | Valid email, empty password — browser validation appears")
    public void testEmptyPassword() {
        LoginPage loginPage = openLoginPage();
        loginPage.enterEmail(VALID_EMAIL)
                 .enterPassword("")
                 .clickLogin();

        // Soft — validation shown AND validation text are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(loginPage.isPasswordValidationMessageDisplayed())
                    .as("Expected password validation message to be shown")
                    .isTrue();
            soft.assertThat(loginPage.getPasswordValidationMessage())
                    .as("Expected browser native validation text")
                    .isEqualTo("Please fill out this field.");
        });
    }

    @Test(description = "TC-05 | Empty email, valid password — browser validation appears")
    public void testEmptyEmail() {
        LoginPage loginPage = openLoginPage();
        loginPage.enterEmail("")
                 .enterPassword(VALID_PASSWORD)
                 .clickLogin();

        // Hard — single check
        assertThat(loginPage.isEmailValidationMessageDisplayed())
                .as("Expected email validation message for empty email field")
                .isTrue();
    }
}
