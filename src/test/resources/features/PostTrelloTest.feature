Feature: Post elements in Trello API
  As a user in Trello, I want to create different elements in my account

  Scenario: Post a new list in a board
    Given the user wants to create a new list "Q/A" in a board
    When the user sends the request to create the list
    Then the Trello API should response with the new list

  Scenario: Post a new card in a list
    Given the user wants to create a new card "MI TARJETA NUEVA" in the "TODO" list
    When the user sends the request to create the card
    Then the Trello API should response with the new card

  Scenario Outline: Post a new comment in a card
    Given the user wants to create a comment "<comment>" in a card "MI TARJETA NUEVA" on the list "TODO"
    When the user sends the request to create the new comment
    Then the Trello API should response with the new comment
    Examples:
      |comment            |
      |TODO COMMENT       |
      |IN PROGRESS COMMENT|
      |DONE_COMMENT       |

  Scenario Outline: Post a member to a card
    Given the user wants to add a member in the card "MI TARJETA NUEVA" on the list "<list>"
    When the user sends the request to add a member in a card
    Then the Trello API should response with the added member
    Examples:
      |list  |
      |TODO  |
