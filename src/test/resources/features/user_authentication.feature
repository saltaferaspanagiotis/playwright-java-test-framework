@smoke @user_authentication @feature-priority:High
Feature: User Authentication

  As a customer,
  I want to be able to register, login, and logout
  So that I can access my personal account and manage my orders.

  Jane is an online shopper

  Rule: New customers should be able to register an account

    @UA_1 @scenario-priority:High
    Scenario: Jane registers a new account with valid details
      Given Jane navigates to the registration page
      When she fills in her registration details with valid information
      And she clicks the Register button
      Then she should be redirected to the login page

  Rule: Registered customers should be able to login
    @UA_2 @scenario-priority:High
    Scenario: Jane logs in with valid credentials
      Given a new user account has been created via the API
      And Jane navigates to the login page
      When she logs in with the registered credentials
      Then she should be on the "My account" page

    @UA_3 @scenario-priority:Medium
    Scenario Outline: Jane tries different invalid credential combinations
      Given Jane navigates to the login page
      When she logs in with email "<email>" and password "<password>"
      Then the error message "Invalid email or password" should be displayed
      Examples:
        | email                              | password    |
        | unknown@toolshop.com               | welcome01   |

  Rule: Logged-in customers should be able to sign out
    @UA_4 @scenario-priority:High
    Scenario: Jane signs out of her account
      Given a new user account has been created via the API
      And Jane navigates to the login page
      And she logs in with the registered credentials
      When she signs out from the account menu
      Then she should be redirected to the login page
