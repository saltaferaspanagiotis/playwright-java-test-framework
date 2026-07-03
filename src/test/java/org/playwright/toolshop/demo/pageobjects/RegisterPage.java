package org.playwright.toolshop.demo.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.playwright.toolshop.demo.domain.User;

public class RegisterPage {

    private final Page page;
    private final Locator firstName;
    private final Locator lastName;
    private final Locator dob;
    private final Locator country;
    private final Locator postalCode;
    private final Locator houseNumber;
    private final Locator street;
    private final Locator city;
    private final Locator state;
    private final Locator phone;
    private final Locator email;
    private final Locator password;
    private final Locator registerSubmit;

    public RegisterPage(Page page) {
        this.page = page;
        this.firstName = page.getByTestId("first-name");
        this.lastName = page.getByTestId("last-name");
        this.dob = page.getByTestId("dob");
        this.country = page.getByTestId("country");
        this.postalCode = page.getByTestId("postal_code");
        this.houseNumber = page.getByTestId("house_number");
        this.street = page.getByTestId("street");
        this.city = page.getByTestId("city");
        this.state = page.getByTestId("state");
        this.phone = page.getByTestId("phone");
        this.email = page.getByTestId("email");
        this.password = page.getByTestId("password");
        this.registerSubmit = page.getByTestId("register-submit");
    }

    public void fillRegistrationForm(User user) {
        firstName.fill(user.first_name());
        lastName.fill(user.last_name());
        dob.fill(user.dob());
        country.selectOption(user.address().country());
        postalCode.fill(user.address().postal_code());
        houseNumber.fill(user.address().house_number());
        street.fill(user.address().street());
        city.fill(user.address().city());
        state.fill(user.address().state());
        phone.fill(user.phone());
        email.fill(user.email());
        password.fill(user.password());
    }

    public void submitRegistration() {
        registerSubmit.click();
    }

    public void registerUser(User user) {
        fillRegistrationForm(user);
        submitRegistration();
    }
}
