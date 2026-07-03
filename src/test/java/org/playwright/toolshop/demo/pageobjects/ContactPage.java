package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import io.qameta.allure.Step;

public class ContactPage {

    private final Page page;
    private final Locator subject;
    private final Locator message;
    private final Locator submit;
    private final Locator confirmationMessage;

    public ContactPage(Page page) {
        this.page = page;
        this.subject = page.getByTestId("subject");
        this.message = page.getByTestId("message");
        this.submit = page.getByTestId("contact-submit");
        this.confirmationMessage = page.locator(".alert-success");
    }

    public boolean isGreetingDisplayedFor(String firstName, String lastName) {
//        page.waitForResponse(resp ->
//                resp.url().contains("/users/me") &&
//                        resp.request().method().equals("GET") &&
//                        resp.status() == 200,
//                () -> {});
        page.waitForLoadState(LoadState.NETWORKIDLE);
        return page.getByText("Hello " + firstName + " " + lastName).isVisible();
    }

    public void fillRequest(String subjectLabel, String messageText) {
        subject.selectOption(new SelectOption().setLabel(subjectLabel));
        message.fill(messageText);
    }

    public void submitRequest() {
        submit.click();
    }

    public String getConfirmationMessage() {
        confirmationMessage.waitFor();
        return confirmationMessage.textContent().trim();
    }
}
