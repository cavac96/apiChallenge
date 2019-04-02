Feature: Update elements in Trello API
  As a user, I want to update elements in my account

  Scenario Outline: Move card to other list
    Given the user wants to move the card "MI TARJETA NUEVA" from "<from>" to "<to>"
    When the user sends the put request to move the card
    Then the Trello API should response moving the card
    Examples:
      |from         |to         |
      |TODO         |IN PROGRESS |
      |IN PROGRESS  |DONE        |