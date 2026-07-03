@smoke @shopping_cart @feature-priority:High
Feature: Shopping Cart

  As a customer,
  I want to add products to my cart
  So that I can review them before checking out.

  Sally is an online shopper

  Rule: Customers should be able to add a product to their cart
    @SC_1 @scenario-priority:High
    Scenario: Sally adds a product to her cart
      Given Sally is on the home page
      When she searches for "Adjustable Wrench"
      And she views the "Adjustable Wrench" product details
      And she adds the product to her cart
      And she opens the cart
      Then the cart should contain the following items:
        | Product           | Quantity | Price  | Total  |
        | Adjustable Wrench | 1        | $20.33 | $20.33 |
