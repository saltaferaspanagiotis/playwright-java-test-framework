@smoke @product_catalog @feature-priority:High
Feature: Product Catalog

  As a customer,
  I want to easily search, filter, and sort products in the catalog
  So that I can find what I need quickly.

  Sally is an online shopper

  Rule: Customers should be able to search for products by name
    @PC_1 @scenario-priority:High
    Scenario: Sally searches for an Adjustable Wrench
      Given Sally is on the home page
      When she searches for "Adjustable Wrench"
      Then the "Adjustable Wrench" product should be displayed

    @PC_2 @scenario-priority:High
    Scenario: Sally searches for a more general term
      Given Sally is on the home page
      When she searches for "saw"
      Then the following products should be displayed:
        | Product      | Price  |
        | Wood Saw     | $12.18 |
        | Circular Saw | $80.19 |

    @PC_3 @scenario-priority:Medium
    Scenario: Sally searches for a product that doesn't exist
      Given Sally is on the home page
      When she searches for "Product-Does-Not-Exist"
      Then no products should be displayed
      And the message "There are no products found." should be displayed


  Rule: Customers should be able to narrow downs their search by category
    @PC_4 @scenario-priority:Medium
    Scenario: Sally filters by Hand Saws
      Given Sally is on the home page
      When she searches for "saw"
      And she filters by "Hand Saw"
      Then the following products should be displayed:
        | Product  | Price  |
        | Wood Saw | $12.18 |

    @PC_5 @scenario-priority:Medium
    Scenario: Sally filters by Power Drills
      Given Sally is on the home page
      When she searches for "drill"
      And she filters by "Power Tools"
      Then the following products should be displayed:
        | Product  | Price  |
        | Cordless Drill 24V | $66.54 |
        | Cordless Drill 12V | $46.50 |

  Rule: Customers should be able to sort products by various criteria
    @PC_6 @scenario-priority:Medium
    Scenario Outline: Sally sorts by different criteria ("<Sort>")
      Given Sally is on the home page
      When she sorts by "<Sort>"
      Then the first product displayed should be "<First Product>"
      Examples:
        | Sort               | First Product       |
        | Name (A - Z)       | Adjustable Wrench   |
        | Name (Z - A)       | Wood Saw            |
        | Price (High - Low) | Drawer Tool Cabinet |
        | Price (Low - High) | Washers             |