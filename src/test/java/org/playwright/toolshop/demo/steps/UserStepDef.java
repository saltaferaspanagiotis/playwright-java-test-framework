package org.playwright.toolshop.demo.steps;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.playwright.toolshop.demo.api.ToolShopAPI;
import org.playwright.toolshop.demo.domain.User;
import org.playwright.toolshop.demo.fixtures.PlaywrightFixture;
import org.playwright.toolshop.demo.fixtures.ScenarioContext;
import org.playwright.toolshop.demo.pageobjects.Login;
import org.playwright.toolshop.demo.pageobjects.NavBar;
import org.playwright.toolshop.demo.pageobjects.RegisterPage;
import org.playwright.toolshop.demo.pageobjects.UserHomePage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class UserStepDef {


    NavBar navBar;
    Login login;
    RegisterPage registerPage;
    UserHomePage userHomePage;
    ToolShopAPI toolShopAPI;

    @Before
    public void setupPageObjects() {
        navBar = new NavBar(PlaywrightFixture.getPage());
        login = new Login(PlaywrightFixture.getPage());
        registerPage = new RegisterPage(PlaywrightFixture.getPage());
        userHomePage = new UserHomePage(PlaywrightFixture.getPage());
        toolShopAPI = new ToolShopAPI(PlaywrightFixture.getPage());
    }


    @Given("Jane navigates to the registration page")
    public void jane_navigates_to_the_registration_page() {
        navBar.navigateRegisterPage();
    }

    @When("she fills in her registration details with valid information")
    public void she_fills_in_her_registration_details_with_valid_information() {
        User user = User.randomValidUser();
        registerPage.fillRegistrationForm(user);
    }

    @When("she clicks the Register button")
    public void she_clicks_the_register_button() {
        registerPage.submitRegistration();
    }

    @Then("she should be redirected to the login page")
    public void she_should_be_redirected_to_the_login_page() {
        Assertions.assertThat(login.isOnLoginPage()).isTrue();
    }

    @Given("a new user account has been created via the API")
    public void a_new_user_account_has_been_created_via_the_api() {
        User user = User.randomValidUser();
        APIResponse response = toolShopAPI.registerUser(user);
        assertThat(response.status()).isEqualTo(201);
        JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);
        assertThat(jsonObject.get("id")).isNotNull();
        assertThat(jsonObject.get("email").getAsString()).isEqualTo(user.email());
        assertThat(jsonObject.get("password")).isNull();
        ScenarioContext.setUser(user);
    }

    @Given("Jane navigates to the login page")
    public void jane_navigates_to_the_login_page() {
        navBar.navigateLoginPage();
    }

    @When("she logs in with email {string} and password {string}")
    public void she_logs_in_with_email_and_password(String email, String password) {
        login.loginWithCredentials(email, password);
    }

    @Then("the error message {string} should be displayed")
    public void the_error_message_should_be_displayed(String errorMessage) {
        Assertions.assertThat(login.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Given("she logs in with the registered credentials")
    public void she_logs_in_with_the_registered_credentials() {
        User user = ScenarioContext.getUser();
        login.loginWithCredentials(user.email(), user.password());
    }

    @Then("she should be on the {string} page")
    public void she_should_be_on_the_page(String pageName) {
        Assertions.assertThat(userHomePage.getPageTitle()).isEqualTo(pageName);
    }

    @When("she signs out from the account menu")
    public void she_signs_out_from_the_account_menu() {
        userHomePage.signOut();
    }
}
