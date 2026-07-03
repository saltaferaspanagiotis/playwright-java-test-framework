package org.playwright.toolshop.demo.steps;

import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.assertj.core.api.Assertions;
import org.playwright.toolshop.demo.domain.CartLineItem;
import org.playwright.toolshop.demo.fixtures.PlaywrightFixture;
import org.playwright.toolshop.demo.pageobjects.CheckoutCart;
import org.playwright.toolshop.demo.pageobjects.NavBar;
import org.playwright.toolshop.demo.pageobjects.ProductDetailsPage;
import org.playwright.toolshop.demo.pageobjects.ProductList;

import java.util.List;
import java.util.Map;

public class ShoppingCartStepDef {

    NavBar navBar;
    ProductList productList;
    ProductDetailsPage productDetailsPage;
    CheckoutCart checkoutCart;

    @Before
    public void setupPageObjects() {
        navBar = new NavBar(PlaywrightFixture.getPage());
        productList = new ProductList(PlaywrightFixture.getPage());
        productDetailsPage = new ProductDetailsPage(PlaywrightFixture.getPage());
        checkoutCart = new CheckoutCart(PlaywrightFixture.getPage());
    }

    @And("she views the {string} product details")
    public void she_views_the_product_details(String productName) {
        productList.viewProductDetails(productName);
    }

    @And("she adds the product to her cart")
    public void she_adds_the_product_to_her_cart() {
        productDetailsPage.addToCart();
    }

    @And("she opens the cart")
    public void she_opens_the_cart() {
        navBar.openCart();
    }

    @DataTableType
    public CartLineItem cartLineItemRow(Map<String, String> row) {
        return new CartLineItem(
                row.get("Product"),
                Integer.parseInt(row.get("Quantity")),
                Double.parseDouble(row.get("Price").replace("$", "")),
                Double.parseDouble(row.get("Total").replace("$", ""))
        );
    }

    @Then("the cart should contain the following items:")
    public void the_cart_should_contain_the_following_items(List<CartLineItem> expectedLineItems) {
        List<CartLineItem> actualLineItems = checkoutCart.getLineItems();
        Assertions.assertThat(actualLineItems).containsExactlyInAnyOrderElementsOf(expectedLineItems);
    }
}
