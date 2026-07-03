package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.playwright.toolshop.demo.config.Endpoint;

public class NavBar {

    private final Page page;
    private final Locator cartLink;
    private final Locator signInLink;
    private final Locator registerLink;

    public NavBar(Page page) {
        this.page = page;
        this.cartLink = page.getByTestId("nav-cart");
        this.signInLink = page.getByTestId("nav-sign-in");
        this.registerLink = page.getByTestId("register-link");
    }

    public void openCart() {
        cartLink.click();
    }

    public void navigateLoginPage() {
        openHomePage();
        signInLink.click();
    }

    public void navigateRegisterPage() {
        openHomePage();
        navigateLoginPage();
        registerLink.click();
    }

    public void openHomePage() {
        page.navigate(Endpoint.HOME.url());
    }

    public void openContactPage() {
        page.navigate(Endpoint.CONTACT.url());
    }
}
