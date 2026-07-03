package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.playwright.toolshop.demo.config.Endpoint;

public class Login {

    private final Page page;
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator loginSubmit;
    private final Locator loginError;

    public Login(Page page) {
        this.page = page;
        this.emailInput = page.getByTestId("email");
        this.passwordInput = page.getByTestId("password");
        this.loginSubmit = page.getByTestId("login-submit");
        this.loginError = page.getByTestId("login-error");
    }

    public void loginWithCredentials(String email, String password) {
        emailInput.fill(email);
        passwordInput.fill(password);
        loginSubmit.click();
    }

    public String getErrorMessage() {
        loginError.waitFor();
        return loginError.textContent().trim();
    }

    public boolean isOnLoginPage() {
        loginSubmit.waitFor();
        return page.url().contains(Endpoint.LOGIN.path());
    }
}
