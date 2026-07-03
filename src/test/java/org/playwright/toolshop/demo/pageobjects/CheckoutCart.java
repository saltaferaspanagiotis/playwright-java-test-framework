package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import org.playwright.toolshop.demo.domain.CartLineItem;
import java.util.List;

public class CheckoutCart {

    private final Page page;
    private final Locator lineItemRows;

    public CheckoutCart(Page page) {
        this.page = page;
        this.lineItemRows = page.locator("app-cart tbody tr");
    }

    public List<CartLineItem> getLineItems() {
        lineItemRows.first().waitFor();
        return lineItemRows
                .all()
                .stream()
                .map(
                        row -> {
                            String title = trimmed(row.getByTestId("product-title").innerText());
                            int quantity = Integer.parseInt(row.getByTestId("product-quantity").inputValue());
                            double price = Double.parseDouble(price(row.getByTestId("product-price").innerText()));
                            double linePrice = Double.parseDouble(price(row.getByTestId("line-price").innerText()));
                            return new CartLineItem(title, quantity, price, linePrice);
                        }
                ).toList();
    }

    private String trimmed(String value) {
        return value.strip().replaceAll(" ", "");
    }

    private String price(String value) {
        return value.replace("$", "");
    }

}
