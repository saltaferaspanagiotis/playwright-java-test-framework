package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class SearchComponent {

    private final Page page;
    private final Locator searchQuery;
    private final Locator searchSubmit;
    private final Locator searchReset;
    private final Locator sort;

    public SearchComponent(Page page) {
        this.page = page;
        this.searchQuery = page.locator("#search-query");
        this.searchSubmit = page.getByTestId("search-submit");
        this.searchReset = page.getByTestId("search-reset");
        this.sort = page.getByTestId("sort");
    }

    public void searchBy(String searchTerm) {
        searchQuery.fill(searchTerm);
        page.waitForResponse("**/products/search**", () -> {
            searchSubmit.click();
        });
    }

    public void clearSearch() {
        page.waitForResponse("**/products**", () -> {
            searchReset.click();
        });
    }

    public void filterBy(String filterName) {
        page.waitForResponse("**/products?**by_category=**", () -> {
            page.getByLabel(filterName).click();
        });
    }

    public void sortBy(String sortFilter) {
        page.waitForResponse("**/products?page=0&sort=**", () -> {
            sort.selectOption(sortFilter);
        });
    }
}
