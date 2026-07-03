package org.playwright.toolshop.demo.steps;

import com.microsoft.playwright.APIResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.playwright.toolshop.demo.api.ToolShopAPI;
import org.playwright.toolshop.demo.domain.User;
import org.playwright.toolshop.demo.fixtures.PlaywrightFixture;
import org.playwright.toolshop.demo.fixtures.ScenarioContext;
import org.playwright.toolshop.demo.pageobjects.ContactPage;
import org.playwright.toolshop.demo.pageobjects.NavBar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CustomerServiceStepDef {

    NavBar navBar;
    ContactPage contactPage;
    ToolShopAPI toolShopAPI;

    @Before
    public void setupPageObjects() {
        navBar = new NavBar(PlaywrightFixture.getPage());
        contactPage = new ContactPage(PlaywrightFixture.getPage());
        toolShopAPI = new ToolShopAPI(PlaywrightFixture.getPage());
    }

    @Given("Jane has registered and logged in via the API")
    public void jane_has_registered_and_logged_in_via_the_api() {
        User user = User.randomValidUser();
        APIResponse response = toolShopAPI.registerUser(user);
        assertThat(response.status()).isEqualTo(201);
        ScenarioContext.setUser(user);
        toolShopAPI.loginAsUser(user.email(), user.password());
    }

    @When("she navigates to the contact page")
    public void she_navigates_to_the_contact_page() {
        navBar.openContactPage();
    }

    @Then("she should be greeted by name on the contact page")
    public void she_should_be_greeted_by_name_on_the_contact_page() {
        User user = ScenarioContext.getUser();
        Assertions.assertThat(contactPage.isGreetingDisplayedFor(user.first_name(), user.last_name())).isTrue();
    }

    @And("she submits a {string} request with the message {string}")
    public void she_submits_a_request_with_the_message(String subject, String message) {
        contactPage.fillRequest(subject, message);
        contactPage.submitRequest();
    }

    @Then("the confirmation message {string} should be displayed")
    public void the_confirmation_message_should_be_displayed(String expectedMessage) {
        Assertions.assertThat(contactPage.getConfirmationMessage()).isEqualTo(expectedMessage);
    }
}
