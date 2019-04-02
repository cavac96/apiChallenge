Feature: Delete elements in Trello API
  As a user, I want to delete an element of my account

  Scenario: Delete a card
    Given the user wants to delete the card "MI TARJETA NUEVA" in "DONE"
    When the user sends the request to delete the card
    Then the Trello API should response with deleting the card