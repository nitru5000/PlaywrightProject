package Utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

public class LocatorHelper {

    private final Page page;

    public LocatorHelper(Page page) {
        this.page = page;
    }

    /**************** Locator Strategies ****************/

    // Raw CSS / selector string
    public Locator locator(String selector) {
        return page.locator(selector);
    }

    // CSS selector
    public Locator byCss(String css) {
        return page.locator(css);
    }

    // ID attribute
    public Locator byId(String id) {
        return page.locator("#" + id);
    }

    // Visible text content
    public Locator byText(String text) {
        return page.getByText(text);
    }

    // Exact text match
    public Locator byExactText(String text) {
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }

    // Placeholder attribute
    public Locator byPlaceholder(String placeholder) {
        return page.getByPlaceholder(placeholder);
    }

    // ARIA role with optional name — FIX: now correctly applies the exact parameter
    public Locator byRole(AriaRole role, String name, boolean exact) {
        if (name != null && !name.trim().isEmpty()) {
            return page.getByRole(role, new Page.GetByRoleOptions()
                    .setName(name)
                    .setExact(exact));  // was always false before — now uses actual param
        }
        return page.getByRole(role);
    }

    // ARIA role without name
    public Locator byRole(AriaRole role) {
        return page.getByRole(role);
    }

    // data-testid attribute
    public Locator byTestId(String testId) {
        return page.getByTestId(testId);
    }

    // Label text
    public Locator byLabel(String label) {
        return page.getByLabel(label);
    }

    // Alt text (images)
    public Locator byAltText(String altText) {
        return page.getByAltText(altText);
    }

    // XPath
    public Locator byXPath(String xpath) {
        return page.locator("xpath=" + xpath);
    }

    /**************** Visibility Checks ****************/

    public boolean isVisible(Locator locator) {
        try {
            return locator.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    // isVisible with explicit timeout — waits up to timeoutMs before returning false
    public boolean isVisible(Locator locator, int timeoutMs) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(timeoutMs));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
