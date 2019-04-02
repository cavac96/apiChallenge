Feature: Get elements in Trello API
  As a user in Trello, I want to get the different elements in my account

  Scenario: Get the boards of an user
    Given the user wants to get their boards
    When the user sends the request to get the boards
    Then the Trello API should response with their list of boards

  Scenario: Get the lists of a board
    Given the user wants to get the lists of a board
    When the user sends the request to get the lists of a board
    Then the Trello API should response with the lists of a board

  Scenario: Get the list of cards
    Given the user wants to get their cards of the list "TODO"
    When the user sends the request to get the cards
    Then the Trello API should response with the list of cards