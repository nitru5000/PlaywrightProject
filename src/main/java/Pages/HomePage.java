package Pages;

import com.microsoft.playwright.Page;
import Utils.LogsUtil;
import com.microsoft.playwright.options.AriaRole;

public class HomePage extends BasePage {

    public HomePage(Page page) {
        super(page, "HomePage");
        LogsUtil.info("HomePage initialized.");
    }

    /**************** Core Navigation ****************/

    public HomePage navigateToHomePage() {
        String url = jsonFile.getJsonData("url");
        page.navigate(url);
        LogsUtil.info("Navigated to: " + url);
        return this;
    }

    /**************** Cookie Handling ****************/

    public HomePage acceptCookies() {
        try {
            locator.byRole(AriaRole.BUTTON, "Accept All", true).click();
            LogsUtil.info("Accepted cookies");
        } catch (Exception e) {
            LogsUtil.debug("No cookies to accept");
        }
        return this;
    }

    /**************** Header Actions ****************/

    public HomePage clickLogo() {
        locator.byTestId(jsonFile.getJsonData("header.logo")).click();
        LogsUtil.info("Clicked logo");
        page.waitForTimeout(2000);
        return this;
    }

    public HomePage searchProduct(String searchTerm) {
        LogsUtil.info("Searching for: " + searchTerm);
        locator.byPlaceholder(jsonFile.getJsonData("header.searchInput")).type(searchTerm);
        page.waitForSelector("p.font-medium.text-sm.truncate:has-text('" + searchTerm + "')",
                new Page.WaitForSelectorOptions().setTimeout(2000));
        locator.byText(searchTerm).click();
        return this;
    }

    public HomePage selectCurrency(String currencyCode) {
        locator.byTestId(jsonFile.getJsonData("header.currencySelector")).click();
        page.waitForTimeout(100);
        locator.byRole(AriaRole.OPTION, currencyCode, false).click();
        LogsUtil.info("Selected currency: " + currencyCode);
        return this;
    }

    public HomePage goToHome() {
        locator.byTestId(jsonFile.getJsonData("header.homeLink")).click();
        LogsUtil.info("Clicked home button");
        page.waitForTimeout(2000);
        return this;
    }

    public WishlistPage goToWishlist() {
        locator.byTestId(jsonFile.getJsonData("header.wishlistLink")).click();
        LogsUtil.info("Went to wishlist");
        return new WishlistPage(page);
    }

    public CartPage goToCart() {
        locator.byTestId(jsonFile.getJsonData("header.cartLink")).click();
        LogsUtil.info("Went to cart");
        CartPage cartPage = new CartPage(page);
        cartPage.waitForCartToLoad();
        return cartPage;
    }

    public LoginPage clickHeaderLogin() {
        locator.byTestId(jsonFile.getJsonData("header.loginButton")).click();
        LogsUtil.info("Clicked login button");
        return new LoginPage(page);
    }

    public HomePage lightMode(String lightMode) {
        locator.byRole(AriaRole.BUTTON, "Toggle theme", false).click();
        locator.byRole(AriaRole.MENUITEM, lightMode, false).click();
        LogsUtil.info("Toggled theme to: " + lightMode);
        return this;
    }

    public HomePage clickDotLog() {
        locator.byRole(AriaRole.BUTTON, "Back to dotLog", false).click();
        LogsUtil.info("Clicked Back to dotLog");
        return this;
    }

    public HomePage clickLogout() {
        locator.byTestId(jsonFile.getJsonData("header.logoutButton")).click();
        LogsUtil.info("Clicked logout button");
        page.waitForTimeout(500);
        return this;
    }

    /**************** Category Navigation ****************/

    public HomePage selectElectronicsCategory(String name) {
        locator.byTestId(jsonFile.getJsonData("navigation.electronics")).click();
        page.waitForTimeout(300);
        locator.byRole(AriaRole.LINK, name, true).click();
        LogsUtil.info("Selected Electronics > " + name);
        return this;
    }

    public HomePage selectCLothingCategory(String name) {
        locator.byTestId(jsonFile.getJsonData("navigation.clothing")).click();
        page.waitForTimeout(300);
        locator.byRole(AriaRole.LINK, name, true).click();
        LogsUtil.info("Selected Clothing > " + name);
        return this;
    }

    public HomePage selectSportsAndFitnessCategory(String name) {
        locator.byTestId(jsonFile.getJsonData("navigation.sports")).click();
        page.waitForTimeout(300);
        locator.byRole(AriaRole.LINK, name, true).click();
        LogsUtil.info("Selected Sports & Fitness > " + name);
        return this;
    }

    public HomePage selectBooksCategory(String books) {
        locator.byTestId(jsonFile.getJsonData("navigation.books")).click();
        page.waitForTimeout(300);
        locator.byRole(AriaRole.LINK, books, true).click();
        LogsUtil.info("Selected Books > " + books);
        return this;
    }

    public HomePage selectFoodAndBeveragesCategory(String foods) {
        locator.byTestId(jsonFile.getJsonData("navigation.food")).click();
        page.waitForTimeout(300);
        locator.byRole(AriaRole.LINK, foods, true).click();
        LogsUtil.info("Selected Food & Beverages > " + foods);
        return this;
    }

    /**************** Banner Actions ****************/

    public HomePage nextBannerSlide() {
        locator.byTestId(jsonFile.getJsonData("banner.nextSlide")).click();
        LogsUtil.info("Clicked next banner slide");
        return this;
    }

    public HomePage previousBannerSlide() {
        locator.byTestId(jsonFile.getJsonData("banner.prevSlide")).click();
        LogsUtil.info("Clicked previous banner slide");
        return this;
    }

    public HomePage clickBannerDot(Integer index) {
        locator.byTestId(jsonFile.getJsonData("banner.dots") + index.toString()).click();
        LogsUtil.info("Clicked banner dot " + index);
        return this;
    }

    /**************** Sort ****************/

    public HomePage sortBy(int sortOption) {
        locator.byText("Sort By").first().click();
        page.waitForTimeout(500);
        switch (sortOption) {
            case 1 -> { locator.byText("Defualt").first().click();            LogsUtil.info("Sorted by: Default"); }
            case 2 -> { locator.byText("Price: Low to High").first().click(); LogsUtil.info("Sorted by: Price Low to High"); }
            case 3 -> { locator.byText("Price: High to Low").first().click(); LogsUtil.info("Sorted by: Price High to Low"); }
            case 4 -> { locator.byText("Name: A to Z").first().click();       LogsUtil.info("Sorted by: Name A to Z"); }
            case 5 -> { locator.byText("Name: Z to A").first().click();       LogsUtil.info("Sorted by: Name Z to A"); }
            default -> {
                LogsUtil.error("Invalid sort option: " + sortOption);
                throw new IllegalArgumentException("Invalid sort option: " + sortOption);
            }
        }
        return this;
    }

    /**************** Filter Actions ****************/

    public HomePage filterBySearch(String searchTerm) {
        locator.byPlaceholder("Search...").type(searchTerm,
                new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Filtered by search: " + searchTerm);
        return this;
    }

    public HomePage filterByPrice(String min, String max) {
        locator.byPlaceholder("min").type(min,
                new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        locator.byPlaceholder("Max").type(max,
                new com.microsoft.playwright.Locator.TypeOptions().setDelay(100));
        LogsUtil.info("Filtered price: " + min + " - " + max);
        return this;
    }

    public HomePage selectCategoryFilter(int category) {
        locator.byTestId(jsonFile.getJsonData("filters.category")).click();
        page.waitForTimeout(100);
        switch (category) {
            case 1 -> { locator.byText("All Categories").nth(1).click();   LogsUtil.info("Filter: All Categories"); }
            case 2 -> { locator.byText("Electronics").last().click();      LogsUtil.info("Filter: Electronics"); }
            case 3 -> { locator.byText("Clothing").last().click();         LogsUtil.info("Filter: Clothing"); }
            case 4 -> { locator.byText("Books").last().click();            LogsUtil.info("Filter: Books"); }
            case 5 -> { locator.byText("Food & Beverage").last().click();  LogsUtil.info("Filter: Food & Beverage"); }
            case 6 -> { locator.byText("Sports & Fitness").last().click(); LogsUtil.info("Filter: Sports & Fitness"); }
            default -> {
                LogsUtil.error("Invalid category: " + category);
                throw new IllegalArgumentException("Invalid category: " + category);
            }
        }
        return this;
    }

    public HomePage selectBrandFilter(int index) {
        locator.byTestId(jsonFile.getJsonData("filters.brand")).click();
        page.waitForTimeout(100);
        switch (index) {
            case 1 -> { locator.byText("Apple").click();          LogsUtil.info("Filter: Apple"); }
            case 2 -> { locator.byText("Samsung").click();        LogsUtil.info("Filter: Samsung"); }
            case 3 -> { locator.byText("Nike").click();           LogsUtil.info("Filter: Nike"); }
            case 4 -> { locator.byText("Harriman House").click(); LogsUtil.info("Filter: Harriman House"); }
            case 5 -> { locator.byText("Organic Food").click();   LogsUtil.info("Filter: Organic Food"); }
            case 6 -> { locator.byText("Fitness Gear").click();   LogsUtil.info("Filter: Fitness Gear"); }
            default -> {
                LogsUtil.error("Invalid brand: " + index);
                throw new IllegalArgumentException("Invalid brand: " + index);
            }
        }
        return this;
    }

    public HomePage resetFilters() {
        locator.byTestId(jsonFile.getJsonData("filters.resetFilters")).click();
        LogsUtil.info("Reset filters");
        return this;
    }

    /**************** Product Actions ****************/

    public HomePage addProductToCart(int index) {
        locator.byText("Add to Cart").nth(index).click();
        LogsUtil.info("Added product index " + index + " to cart");
        // Wait for cart badge to update — confirms item was actually added before navigating
        page.waitForSelector("[data-testid='cart-count-badge']",
                new Page.WaitForSelectorOptions().setTimeout(5000));
        return this;
    }

    public HomePage addToWishlist(int index) {
        locator.byText("Save").nth(index).click();
        LogsUtil.info("Added product index " + index + " to wishlist");
        // Brief wait — no badge indicator for wishlist, allow UI to process the action
        page.waitForTimeout(800);
        return this;
    }

    public HomePage compareProduct(int index) {
        locator.byText("Compare").nth(index).click();
        LogsUtil.info("Compared product index " + index);
        return this;
    }

    public HomePage compareNow() {
        locator.byText("Compare Now").click();
        LogsUtil.info("Clicked Compare Now");
        return this;
    }

    public HomePage clearCompare() {
        locator.byTestId(jsonFile.getJsonData("compare.clearCompare")).click();
        LogsUtil.info("Cleared compare");
        return this;
    }

    public HomePage clearAll() {
        locator.byTestId(jsonFile.getJsonData("compare.clearAll")).click();
        LogsUtil.info("Clicked Clear All");
        return this;
    }

    public ProductPage clickProduct(int index) {
        locator.locator("[data-testid^='product-link-']").nth(index).click();
        LogsUtil.info("Clicked product index " + index);
        page.waitForURL("**/product/**");
        return new ProductPage(page);
    }

    /**************** Pagination ****************/

    public HomePage goToNextPage() {
        locator.byText("Next").last().click();
        LogsUtil.info("Went to next page");
        page.waitForTimeout(3000);
        return this;
    }

    public HomePage goToPreviousPage() {
        locator.byText("Previous").last().click();
        LogsUtil.info("Went to previous page");
        return this;
    }

    public HomePage goToPage(int pageNumber) {
        locator.byTestId("pagination-page-" + pageNumber).click();
        LogsUtil.info("Went to page " + pageNumber);
        return this;
    }

    /**************** Accessibility ****************/

    public HomePage skipToMainContent() {
        try {
            page.locator(jsonFile.getJsonData("selectors.skipToMain")).click();
            LogsUtil.info("Skipped to main content");
        } catch (Exception e) {
            LogsUtil.debug("Skip link not found");
        }
        return this;
    }

    public HomePage skipToNavigation() {
        try {
            page.locator(jsonFile.getJsonData("selectors.skipToNavigation")).click();
            LogsUtil.info("Skipped to navigation");
        } catch (Exception e) {
            LogsUtil.debug("Skip navigation link not found");
        }
        return this;
    }

    /**************** Category Nav Bar ****************/

    public HomePage clickNavElectronics() {
        locator.byTestId(jsonFile.getJsonData("navigation.electronics")).click();
        LogsUtil.info("Clicked nav: Electronics");
        return this;
    }

    public HomePage clickNavClothing() {
        locator.byTestId(jsonFile.getJsonData("navigation.clothing")).click();
        LogsUtil.info("Clicked nav: Clothing");
        return this;
    }

    public HomePage clickNavSports() {
        locator.byTestId(jsonFile.getJsonData("navigation.sports")).click();
        LogsUtil.info("Clicked nav: Sports & Fitness");
        return this;
    }

    public HomePage clickNavBooks() {
        locator.byTestId(jsonFile.getJsonData("navigation.books")).click();
        LogsUtil.info("Clicked nav: Books");
        return this;
    }

    public HomePage clickNavFood() {
        locator.byTestId(jsonFile.getJsonData("navigation.food")).click();
        LogsUtil.info("Clicked nav: Food & Beverages");
        return this;
    }

    /**************** State Queries ****************/

    public boolean isOnHomePage() {
        String expectedUrl = jsonFile.getJsonData("url");
        boolean result     = page.url().contains(expectedUrl);
        if (result) LogsUtil.info("isOnHomePage: true");
        else        LogsUtil.error("isOnHomePage: false — URL: " + page.url());
        return result;
    }

    public boolean isOnCategoryPage(String categoryParam) {
        boolean result = page.url().contains("category=" + categoryParam);
        if (result) LogsUtil.info("isOnCategoryPage: category=" + categoryParam + " found");
        else        LogsUtil.error("isOnCategoryPage: category=" + categoryParam + " NOT in URL: " + page.url());
        return result;
    }

    public boolean isOnCategoryBrandPage(String categoryParam, String brandParam) {
        String url     = page.url();
        boolean result = url.contains("category=" + categoryParam) && url.contains("brand=" + brandParam);
        if (result) LogsUtil.info("isOnCategoryBrandPage: " + categoryParam + " + " + brandParam);
        else        LogsUtil.error("isOnCategoryBrandPage: NOT in URL: " + url);
        return result;
    }

    public boolean isLoggedIn() {
        boolean result = locator.isVisible(locator.byTestId(jsonFile.getJsonData("header.logoutButton")), 3000);
        if (result) LogsUtil.info("isLoggedIn: true");
        else        LogsUtil.error("isLoggedIn: false — logout button not found");
        return result;
    }

    public boolean isHeaderVisible() {
        boolean result = locator.isVisible(locator.byTestId(jsonFile.getJsonData("header.testId")), 3000);
        if (result) LogsUtil.info("isHeaderVisible: true");
        else        LogsUtil.error("isHeaderVisible: false");
        return result;
    }

    public boolean isMainNavigationVisible() {
        boolean result = locator.isVisible(locator.byTestId(jsonFile.getJsonData("navigation.testId")), 3000);
        if (result) LogsUtil.info("isMainNavigationVisible: true");
        else        LogsUtil.error("isMainNavigationVisible: false");
        return result;
    }

    public boolean isBannerVisible() {
        boolean result = locator.isVisible(locator.byTestId(jsonFile.getJsonData("banner.hero")), 3000);
        if (result) LogsUtil.info("isBannerVisible: true");
        else        LogsUtil.error("isBannerVisible: false");
        return result;
    }

    public boolean isFilterPanelVisible() {
        boolean result = locator.isVisible(locator.byTestId(jsonFile.getJsonData("filters.testId")), 3000);
        if (result) LogsUtil.info("isFilterPanelVisible: true");
        else        LogsUtil.error("isFilterPanelVisible: false");
        return result;
    }

    public boolean isProductGridVisible() {
        boolean result = locator.isVisible(locator.locator("[data-testid^='product-card-']").first(), 5000);
        if (result) LogsUtil.info("isProductGridVisible: true");
        else        LogsUtil.error("isProductGridVisible: false");
        return result;
    }

    public int getProductCount() {
        int count = locator.locator("[data-testid^='product-card-']").count();
        LogsUtil.info("getProductCount: " + count);
        return count;
    }

    public String getSelectedCurrency() {
        String currency = locator.byTestId(jsonFile.getJsonData("header.currencySelector")).textContent().trim();
        LogsUtil.info("getSelectedCurrency: " + currency);
        return currency;
    }

    public int getCurrentPageNumber() {
        try {
            String text = page.locator("[data-testid^='pagination-page-'][data-variant='default']")
                    .textContent().trim();
            int num = Integer.parseInt(text);
            LogsUtil.info("getCurrentPageNumber: " + num);
            return num;
        } catch (Exception e) {
            LogsUtil.error("getCurrentPageNumber: could not determine active page");
            return -1;
        }
    }

    public boolean isPreviousPageButtonDisabled() {
        boolean result = page.locator("[data-testid='pagination-prev']").isDisabled();
        if (result) LogsUtil.info("isPreviousPageButtonDisabled: true");
        else        LogsUtil.error("isPreviousPageButtonDisabled: false");
        return result;
    }

    public boolean isPriceSortedLowToHigh() {
        try {
            double p1 = Double.parseDouble(locator.locator("[data-testid^='product-price-']").nth(0).textContent().replaceAll("[^0-9.]", ""));
            double p2 = Double.parseDouble(locator.locator("[data-testid^='product-price-']").nth(1).textContent().replaceAll("[^0-9.]", ""));
            boolean result = p1 <= p2;
            LogsUtil.info("isPriceSortedLowToHigh: " + p1 + " <= " + p2 + " → " + result);
            return result;
        } catch (Exception e) {
            LogsUtil.error("isPriceSortedLowToHigh: failed — " + e.getMessage());
            return false;
        }
    }

    public boolean isPriceSortedHighToLow() {
        try {
            double p1 = Double.parseDouble(locator.locator("[data-testid^='product-price-']").nth(0).textContent().replaceAll("[^0-9.]", ""));
            double p2 = Double.parseDouble(locator.locator("[data-testid^='product-price-']").nth(1).textContent().replaceAll("[^0-9.]", ""));
            boolean result = p1 >= p2;
            LogsUtil.info("isPriceSortedHighToLow: " + p1 + " >= " + p2 + " → " + result);
            return result;
        } catch (Exception e) {
            LogsUtil.error("isPriceSortedHighToLow: failed — " + e.getMessage());
            return false;
        }
    }

    public boolean isNameSortedAtoZ() {
        try {
            String n1  = locator.locator("[data-testid^='product-name-']").nth(0).textContent().trim();
            String n2  = locator.locator("[data-testid^='product-name-']").nth(1).textContent().trim();
            boolean result = n1.compareToIgnoreCase(n2) <= 0;
            LogsUtil.info("isNameSortedAtoZ: \"" + n1 + "\" <= \"" + n2 + "\" → " + result);
            return result;
        } catch (Exception e) {
            LogsUtil.error("isNameSortedAtoZ: failed — " + e.getMessage());
            return false;
        }
    }

    public boolean isNameSortedZtoA() {
        try {
            String n1  = locator.locator("[data-testid^='product-name-']").nth(0).textContent().trim();
            String n2  = locator.locator("[data-testid^='product-name-']").nth(1).textContent().trim();
            boolean result = n1.compareToIgnoreCase(n2) >= 0;
            LogsUtil.info("isNameSortedZtoA: \"" + n1 + "\" >= \"" + n2 + "\" → " + result);
            return result;
        } catch (Exception e) {
            LogsUtil.error("isNameSortedZtoA: failed — " + e.getMessage());
            return false;
        }
    }

    public HomePage waitForLoading() {
        page.waitForTimeout(2000);
        return this;
    }
}
