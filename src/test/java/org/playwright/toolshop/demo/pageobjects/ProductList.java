package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.playwright.toolshop.demo.domain.ProductSummary;

import java.util.List;

public class ProductList {
    private final Page page;
    private final Locator productNames;
    private final Locator productCards;
    private final Locator searchCompletedMessage;

    public ProductList(Page page) {
        this.page = page;
        this.productNames = page.getByTestId("product-name");
        this.productCards = page.locator(".card");
        this.searchCompletedMessage = page.getByTestId("search_completed");
    }


    public List<String> getProductNames() {
        productNames.first().waitFor();
        return productNames.allInnerTexts();
    }

    public List<ProductSummary> getProductSummaries() {
        productCards.first().waitFor();
        return productCards.all()
                .stream()
                .map(productCard -> {
                    String productName = productCard.getByTestId("product-name").textContent().strip();
                    String productPrice = productCard.getByTestId("product-price").textContent();
                    return new ProductSummary(productName, productPrice);
                }).toList();
    }

    @Step("View product details")
    public void viewProductDetails(String productName) {
        productCards.getByText(productName).click();
    }

    public String getSearchCompletedMessage() {
        return searchCompletedMessage.textContent();
    }
}
