package Pages;

import Utils.JsonUtil;
import Utils.LocatorHelper;
import Utils.LogsUtil;
import com.microsoft.playwright.Page;

/**
 * Industry-standard base class for all page objects.
 * Eliminates the repeated page/locator/jsonFile field declarations
 * that were duplicated across every page object.
 *
 * All page objects extend this and call super(page, "FileName").
 */
public abstract class BasePage {

    protected final Page page;
    protected final LocatorHelper locator;
    protected final JsonUtil jsonFile;

    public BasePage(Page page, String jsonFileName) {
        this.page     = page;
        this.locator  = new LocatorHelper(page);
        this.jsonFile = new JsonUtil(jsonFileName);
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public String getPageTitle() {
        return page.title();
    }
}
