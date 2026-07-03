package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class ProductDetailsPage {

    private final Page page;
    private final Locator addToCartButton;

    public ProductDetailsPage(Page page) {
        this.page = page;
        this.addToCartButton = page.getByTestId("add-to-cart");
    }

    @Step("Add product to cart")
    public void addToCart() {
        addToCartButton.click();
    }
}
