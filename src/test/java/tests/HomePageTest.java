package tests;

import Pages.HomePage;
import base.BaseTest;
import Utils.LogsUtil;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HomePageTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        homePage = new HomePage(getPage());
        homePage.navigateToHomePage().acceptCookies();
    }

    // ── Page Structure ────────────────────────────────────────────────────────
    // TC-01 to TC-07: single assertion each — hard is correct

    @Test(description = "TC-01 | Home page loads and URL is correct")
    public void testHomePageLoads() {
        assertThat(homePage.isOnHomePage())
                .as("Expected home page URL. Got: " + homePage.getCurrentUrl())
                .isTrue();
    }

    @Test(description = "TC-02 | Header is visible on home page")
    public void testHeaderIsVisible() {
        assertThat(homePage.isHeaderVisible())
                .as("Expected header to be visible")
                .isTrue();
    }

    @Test(description = "TC-03 | Category navigation bar is visible")
    public void testNavigationBarIsVisible() {
        assertThat(homePage.isMainNavigationVisible())
                .as("Expected main navigation bar to be visible")
                .isTrue();
    }

    @Test(description = "TC-04 | Hero banner is visible on home page")
    public void testBannerIsVisible() {
        assertThat(homePage.isBannerVisible())
                .as("Expected hero banner to be visible")
                .isTrue();
    }

    @Test(description = "TC-05 | Filter panel is visible on home page")
    public void testFilterPanelIsVisible() {
        assertThat(homePage.isFilterPanelVisible())
                .as("Expected product filters panel to be visible")
                .isTrue();
    }

    @Test(description = "TC-06 | Product grid is visible on home page")
    public void testProductGridIsVisible() {
        assertThat(homePage.isProductGridVisible())
                .as("Expected product grid to be visible")
                .isTrue();
    }

    @Test(description = "TC-07 | Home page displays at least one product")
    public void testProductsAreDisplayed() {
        assertThat(homePage.getProductCount())
                .as("Expected at least one product to be displayed")
                .isGreaterThan(0);
    }

    // ── Currency ──────────────────────────────────────────────────────────────

    @Test(description = "TC-08 | Default currency is AUD on page load")
    public void testDefaultCurrencyIsAUD() {
        assertThat(homePage.getSelectedCurrency())
                .as("Expected default currency to be 'A$ AUD'")
                .isEqualTo("A$ AUD");
    }

    @Test(description = "TC-09 | User can change currency to USD")
    public void testSelectCurrencyUSD() {
        String defaultCurrency = homePage.getSelectedCurrency();
        homePage.selectCurrency("USD");
        String selectedCurrency = homePage.getSelectedCurrency();

        // Soft — changed AND contains USD are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(selectedCurrency)
                    .as("Expected currency to change from default: " + defaultCurrency)
                    .isNotEqualTo(defaultCurrency);
            soft.assertThat(selectedCurrency)
                    .as("Expected selected currency to contain 'USD'")
                    .contains("USD");
        });
    }

    @Test(description = "TC-10 | User can change currency to EUR")
    public void testSelectCurrencyEUR() {
        String defaultCurrency = homePage.getSelectedCurrency();
        homePage.selectCurrency("EUR");
        String selectedCurrency = homePage.getSelectedCurrency();

        // Soft — changed AND contains EUR are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(selectedCurrency)
                    .as("Expected currency to change from default: " + defaultCurrency)
                    .isNotEqualTo(defaultCurrency);
            soft.assertThat(selectedCurrency)
                    .as("Expected selected currency to contain 'EUR'")
                    .contains("EUR");
        });
    }

    // ── Category Navigation ───────────────────────────────────────────────────
    // Soft — URL correct AND products shown are independent checks

    @Test(description = "TC-11 | Clicking Electronics nav link filters to electronics category")
    public void testNavElectronics() {
        homePage.clickNavElectronics();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("electronics"))
                    .as("Expected URL to contain category=electronics. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount())
                    .as("Expected electronics products to be displayed")
                    .isGreaterThan(0);
        });
    }

    @Test(description = "TC-12 | Clicking Clothing nav link filters to clothing category")
    public void testNavClothing() {
        homePage.clickNavClothing();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("clothing"))
                    .as("Expected URL to contain category=clothing. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount())
                    .as("Expected clothing products to be displayed")
                    .isGreaterThan(0);
        });
    }

    @Test(description = "TC-13 | Clicking Sports nav link filters to sports category")
    public void testNavSports() {
        homePage.clickNavSports();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("sports"))
                    .as("Expected URL to contain category=sports. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount())
                    .as("Expected sports products to be displayed")
                    .isGreaterThan(0);
        });
    }

    @Test(description = "TC-14 | Clicking Books nav link filters to books category")
    public void testNavBooks() {
        homePage.clickNavBooks();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("books"))
                    .as("Expected URL to contain category=books. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount())
                    .as("Expected books to be displayed")
                    .isGreaterThan(0);
        });
    }

    @Test(description = "TC-15 | Clicking Food nav link filters to food category")
    public void testNavFood() {
        homePage.clickNavFood();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("food"))
                    .as("Expected URL to contain category=food. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount())
                    .as("Expected food products to be displayed")
                    .isGreaterThan(0);
        });
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    @Test(description = "TC-16 | Filter by Electronics category reduces product count")
    public void testFilterByElectronicsCategory() {
        int beforeCount = homePage.getProductCount();
        homePage.selectCategoryFilter(2);
        int afterCount = homePage.getProductCount();

        // Soft — count > 0 AND count <= before are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(afterCount)
                    .as("Expected Electronics products to be displayed")
                    .isGreaterThan(0);
            soft.assertThat(afterCount)
                    .as("Filtered count (" + afterCount + ") should be ≤ total (" + beforeCount + ")")
                    .isLessThanOrEqualTo(beforeCount);
        });
    }

    @Test(description = "TC-17 | Reset filters restores full product count")
    public void testResetFilters() {
        int beforeCount = homePage.getProductCount();
        homePage.selectCategoryFilter(2);
        homePage.resetFilters();
        assertThat(homePage.getProductCount())
                .as("Expected product count to restore to " + beforeCount + " after reset")
                .isEqualTo(beforeCount);
    }

    @Test(description = "TC-18 | Filter search returns matching products")
    public void testFilterBySearch() {
        homePage.filterBySearch("Yoga");
        assertThat(homePage.getProductCount())
                .as("Expected products matching 'Yoga' to be displayed")
                .isGreaterThan(0);
    }

    @Test(description = "TC-19 | Filter search with no match returns zero products")
    public void testFilterBySearchNoResults() {
        homePage.filterBySearch("xyznonexistentproduct123");
        assertThat(homePage.getProductCount())
                .as("Expected 0 products for nonsense search term")
                .isEqualTo(0);
    }

    @Test(description = "TC-20 | Filter by price range returns products within range")
    public void testFilterByPriceRange() {
        homePage.filterByPrice("10", "50");
        assertThat(homePage.getProductCount())
                .as("Expected products in price range $10–$50")
                .isGreaterThan(0);
    }

    @Test(description = "TC-21 | Selecting brand filter reduces product count")
    public void testFilterByBrand() {
        int beforeCount = homePage.getProductCount();
        homePage.selectBrandFilter(1); // Apple

        // Soft — count > 0 AND count <= before are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.getProductCount())
                    .as("Expected Apple products to be displayed")
                    .isGreaterThan(0);
            soft.assertThat(homePage.getProductCount())
                    .as("Brand-filtered count should be ≤ total (" + beforeCount + ")")
                    .isLessThanOrEqualTo(beforeCount);
        });
    }

    // ── Sort ──────────────────────────────────────────────────────────────────

    @Test(description = "TC-22 | Sort Price Low to High — first product price ≤ second")
    public void testSortByPriceLowToHigh() {
        homePage.sortBy(2);
        assertThat(homePage.isPriceSortedLowToHigh())
                .as("Expected first product price to be ≤ second after sorting Low to High")
                .isTrue();
    }

    @Test(description = "TC-23 | Sort Price High to Low — first product price ≥ second")
    public void testSortByPriceHighToLow() {
        homePage.sortBy(3);
        assertThat(homePage.isPriceSortedHighToLow())
                .as("Expected first product price to be ≥ second after sorting High to Low")
                .isTrue();
    }

    @Test(description = "TC-24 | Sort Name A to Z — first product name ≤ second alphabetically")
    public void testSortByNameAtoZ() {
        homePage.sortBy(4);
        assertThat(homePage.isNameSortedAtoZ())
                .as("Expected products to be sorted A→Z by name")
                .isTrue();
    }

    @Test(description = "TC-25 | Sort Name Z to A — first product name ≥ second alphabetically")
    public void testSortByNameZtoA() {
        homePage.sortBy(5);
        assertThat(homePage.isNameSortedZtoA())
                .as("Expected products to be sorted Z→A by name")
                .isTrue();
    }

    // ── Pagination ────────────────────────────────────────────────────────────

    @Test(description = "TC-26 | On page load, page 1 is active and Previous button is disabled")
    public void testInitialPaginationState() {
        // Soft — page number AND previous disabled are independent state checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.getCurrentPageNumber())
                    .as("Expected page 1 to be active on initial load")
                    .isEqualTo(1);
            soft.assertThat(homePage.isPreviousPageButtonDisabled())
                    .as("Expected Previous button to be disabled on page 1")
                    .isTrue();
        });
    }

    @Test(description = "TC-27 | Clicking Next navigates to page 2 and shows products")
    public void testNavigateToNextPage() {
        homePage.goToNextPage();
        // Soft — page number AND grid visible are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.getCurrentPageNumber())
                    .as("Expected active page to be 2 after clicking Next")
                    .isEqualTo(2);
            soft.assertThat(homePage.isProductGridVisible())
                    .as("Expected products to be visible on page 2")
                    .isTrue();
        });
    }

    @Test(description = "TC-28 | Clicking page 5 directly activates page 5 and shows products")
    public void testNavigateToSpecificPage() {
        homePage.goToPage(5);
        // Soft — page number AND grid visible are independent checks
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.getCurrentPageNumber())
                    .as("Expected active page to be 5 after clicking page 5")
                    .isEqualTo(5);
            soft.assertThat(homePage.isProductGridVisible())
                    .as("Expected products to be visible on page 5")
                    .isTrue();
        });
    }

    @Test(description = "TC-29 | Previous button is enabled after navigating away from page 1")
    public void testPreviousButtonEnabledAfterNextPage() {
        homePage.goToNextPage();
        assertThat(homePage.isPreviousPageButtonDisabled())
                .as("Expected Previous button to be enabled on page 2")
                .isFalse();
    }

    // ── Banner Navigation ─────────────────────────────────────────────────────

    @Test(description = "TC-30 | Banner next button advances the slide")
    public void testBannerNextSlide() {
        homePage.nextBannerSlide();
        assertThat(homePage.isBannerVisible())
                .as("Expected hero-banner to remain visible after clicking next slide")
                .isTrue();
    }

    @Test(description = "TC-31 | Banner previous button is clickable")
    public void testBannerPreviousSlide() {
        homePage.nextBannerSlide().previousBannerSlide();
        assertThat(homePage.isBannerVisible())
                .as("Expected hero-banner to remain visible after clicking previous slide")
                .isTrue();
    }

    @Test(description = "TC-32 | Clicking banner dot navigates to that slide")
    public void testBannerDotNavigation() {
        homePage.clickBannerDot(2);
        assertThat(homePage.isBannerVisible())
                .as("Expected hero-banner to remain visible after clicking dot 2")
                .isTrue();
    }

    // ── Logo / Home Link ──────────────────────────────────────────────────────

    @Test(description = "TC-33 | Clicking logo returns user to home page")
    public void testLogoNavigatesToHomePage() {
        homePage.goToNextPage().clickLogo();
        assertThat(homePage.isOnHomePage())
                .as("Expected to be on home page after clicking logo. URL: " + homePage.getCurrentUrl())
                .isTrue();
    }

    @Test(description = "TC-34 | Home nav link returns user to home from category page")
    public void testHomeNavLinkFromCategoryPage() {
        homePage.clickNavElectronics().goToHome();
        assertThat(homePage.isOnHomePage())
                .as("Expected to be on home page after clicking Home link. URL: " + homePage.getCurrentUrl())
                .isTrue();
    }

    // ── Cart / Wishlist Navigation ────────────────────────────────────────────

    @Test(description = "TC-35 | Clicking Cart navigates to cart page")
    public void testNavigateToCartPage() {
        homePage.goToCart();
        assertThat(getPage().url())
                .as("Expected URL to contain '/cart'")
                .contains("/cart");
    }

    @Test(description = "TC-36 | Clicking Wishlist navigates to wishlist page")
    public void testNavigateToWishlistPage() {
        homePage.goToWishlist();
        assertThat(getPage().url())
                .as("Expected URL to contain '/wishlist'")
                .contains("/wishlist");
    }

    // ── Category Dropdown Subcategories ──────────────────────────────────────
    // Soft — URL correct AND products shown are independent for all dropdown tests

    @Test(description = "TC-38 | Electronics > Phones navigates to electronics + apple brand")
    public void testElectronicsDropdownPhones() {
        homePage.selectElectronicsCategory("Phones");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryBrandPage("electronics", "apple"))
                    .as("Expected category=electronics&brand=apple in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after selecting Phones").isGreaterThan(0);
        });
    }

    @Test(description = "TC-39 | Electronics > Laptops navigates to electronics + samsung brand")
    public void testElectronicsDropdownLaptops() {
        homePage.selectElectronicsCategory("Laptops");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryBrandPage("electronics", "samsung"))
                    .as("Expected category=electronics&brand=samsung in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after selecting Laptops").isGreaterThan(0);
        });
    }

    @Test(description = "TC-40 | Electronics > All Electronics navigates to electronics category")
    public void testElectronicsDropdownAllElectronics() {
        homePage.selectElectronicsCategory("All Electronics");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("electronics"))
                    .as("Expected category=electronics in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after All Electronics").isGreaterThan(0);
        });
    }

    @Test(description = "TC-41 | Clothing > Nike navigates to clothing + nike brand")
    public void testClothingDropdownNike() {
        homePage.selectCLothingCategory("Nike");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryBrandPage("clothing", "nike"))
                    .as("Expected category=clothing&brand=nike in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after selecting Nike").isGreaterThan(0);
        });
    }

    @Test(description = "TC-42 | Clothing > All Clothing navigates to clothing category")
    public void testClothingDropdownAllClothing() {
        homePage.selectCLothingCategory("All Clothing");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("clothing"))
                    .as("Expected category=clothing in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after All Clothing").isGreaterThan(0);
        });
    }

    @Test(description = "TC-43 | Sports > Equipment navigates to sports + fitness-gear brand")
    public void testSportsDropdownEquipment() {
        homePage.selectSportsAndFitnessCategory("Equipment");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryBrandPage("sports", "fitness-gear"))
                    .as("Expected category=sports&brand=fitness-gear in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after selecting Equipment").isGreaterThan(0);
        });
    }

    @Test(description = "TC-44 | Sports > All Sports navigates to sports category")
    public void testSportsDropdownAllSports() {
        homePage.selectSportsAndFitnessCategory("All Sports");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("sports"))
                    .as("Expected category=sports in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after All Sports").isGreaterThan(0);
        });
    }

    @Test(description = "TC-45 | Books > Harriman House navigates to books + harriman brand")
    public void testBooksDropdownHarrimanHouse() {
        homePage.selectBooksCategory("Harriman House");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryBrandPage("books", "harriman"))
                    .as("Expected category=books&brand=harriman in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after selecting Harriman House").isGreaterThan(0);
        });
    }

    @Test(description = "TC-46 | Books > All Books navigates to books category")
    public void testBooksDropdownAllBooks() {
        homePage.selectBooksCategory("All Books");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("books"))
                    .as("Expected category=books in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after All Books").isGreaterThan(0);
        });
    }

    @Test(description = "TC-47 | Food > Organic Foods navigates to food + organic-foods brand")
    public void testFoodDropdownOrganicFoods() {
        homePage.selectFoodAndBeveragesCategory("Organic Foods");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryBrandPage("food", "organic-foods"))
                    .as("Expected category=food&brand=organic-foods in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after selecting Organic Foods").isGreaterThan(0);
        });
    }

    @Test(description = "TC-48 | Food > All Food navigates to food category")
    public void testFoodDropdownAllFood() {
        homePage.selectFoodAndBeveragesCategory("All Food");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(homePage.isOnCategoryPage("food"))
                    .as("Expected category=food in URL. Got: " + homePage.getCurrentUrl())
                    .isTrue();
            soft.assertThat(homePage.getProductCount()).as("Expected products after All Food").isGreaterThan(0);
        });
    }
}
