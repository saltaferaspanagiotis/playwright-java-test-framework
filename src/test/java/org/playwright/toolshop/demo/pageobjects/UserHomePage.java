package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class UserHomePage {

    private final Page page;
    private final Locator pageTitle;
    private final Locator navMenu;
    private final Locator navSignOut;
    private final Locator navSignIn;

    public UserHomePage(Page page) {
        this.page = page;
        this.pageTitle = page.getByTestId("page-title");
        this.navMenu = page.getByTestId("nav-menu");
        this.navSignOut = page.getByTestId("nav-sign-out");
        this.navSignIn = page.getByTestId("nav-sign-in");
    }

    public String getPageTitle() {
        return pageTitle.textContent().trim();
    }

    public void signOut() {
        navMenu.click();
        navSignOut.click();
    }
}
