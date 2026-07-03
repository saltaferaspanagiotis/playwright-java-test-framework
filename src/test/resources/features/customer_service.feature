@smoke @customer_service @feature-priority:High
Feature: Customer Service Requests

  As a registered customer,
  I want my session to carry over when I visit the contact page
  So that I don't have to re-enter my details to submit a request.

  Jane is a registered customer

  Rule: Logged-in customers should be recognized on the contact page
    @CS_1 @scenario-priority:High
    Scenario: Jane submits a customer service request while logged in
      Given Jane has registered and logged in via the API
      When she navigates to the contact page
      Then she should be greeted by name on the contact page
      And she submits a "Customer service" request with the message "I would like help tracking my recent order #134636456"
      Then the confirmation message "Thanks for your message! We will contact you shortly." should be displayed
