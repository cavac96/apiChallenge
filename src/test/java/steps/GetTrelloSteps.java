package steps;

import controllers.GetTestController;
import controllers.PostTestController;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.annotations.Steps;
import org.hamcrest.Matchers;
import org.junit.Assert;
import utils.Constants;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GetTrelloSteps {

    private Logger logger = Logger.getLogger("GetTrelloSteps.class");
    @Steps
    private GetTestController testControllerInstance = GetTestController.GetTestControllerInstance();
    private RequestSpecification requestSpecification;
    private Response response;

    String userId;
    String boardId;
    String listId;
    String listName;

    @Given("^the user wants to get their boards$")
    public void theUserWantsToGetTheirBoards() {
        requestSpecification = testControllerInstance.authenticate();
    }

    @When("^the user sends the request to get the boards$")
    public void theUserSendsTheRequestToGetTheBoards() {
        userId = testControllerInstance.getUserId(requestSpecification);
        response = testControllerInstance.getBoards(requestSpecification, userId);
    }

    @Then("^the Trello API should response with their list of boards$")
    public void theTrelloAPIShouldResponseWithTheirListOfBoards() {
        writeOnLog("boards > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: Could not get the boards.", response.getBody().asString(), Matchers.not(Matchers.equalTo("model not found")));
    }

    @Given("^the user wants to get the lists of a board$")
    public void theUserWantsToGetTheListsOfABoard() {
        requestSpecification = testControllerInstance.authenticate();
        userId = testControllerInstance.getUserId(requestSpecification);
        response = testControllerInstance.getBoards(requestSpecification, userId);
    }

    @When("^the user sends the request to get the lists of a board$")
    public void theUserSendsTheRequestToGetTheListsOfABoard() {
        boardId = testControllerInstance.getBoardId(requestSpecification, Constants.BOARD);
        response = testControllerInstance.getListsOfBoard(requestSpecification, boardId);
    }

    @Then("^the Trello API should response with the lists of a board$")
    public void theTrelloAPIShouldResponseWithTheListOfABoard() {
        writeOnLog("lists > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: Could not get the lists.", response.getBody().asString(), Matchers.not(Matchers.equalTo("model not found")));
        Assert.assertThat("Error: Could not get the lists.", response.getBody().asString(), Matchers.not(Matchers.equalTo("The requested resource was not found.")));
    }

    @Given("^the user wants to get their cards of the list \"([^\"]*)\"$")
    public void theUserWantsToGetTheirCardsOfAList(String listName) {
        this.listName = listName;

        requestSpecification = testControllerInstance.authenticate();
        userId = testControllerInstance.getUserId(requestSpecification);

        response = testControllerInstance.getBoards(requestSpecification, userId);
        boardId = testControllerInstance.getBoardId(requestSpecification, Constants.BOARD);

        response = testControllerInstance.getListsOfBoard(requestSpecification, boardId);
        listId = testControllerInstance.getListId(requestSpecification, listName);


        //verify if the list exists. if not, create it
        requestSpecification = PostTestController.PostTestControllerInstance().verifyListId(requestSpecification, listId, listName);
        listId = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName);
    }

    @When("^the user sends the request to get the cards")
    public void theUserSendsTheRequestToGetTheCardsOfTheList() {
        response = testControllerInstance.getCards(requestSpecification, listId);
    }

    @Then("^the Trello API should response with the list of cards$")
    public void theTrelloAPIShouldResponseWithTheListOfCards() {
        writeOnLog("cards > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: Could not get the cards.", response.getBody().asString(), Matchers.not(Matchers.equalTo("invalid id")));
        Assert.assertThat("Error: Could not get the cards.", response.getBody().asString(), Matchers.not(Matchers.equalTo("model not found")));
        Assert.assertThat("Error: Could not get the cards.", response.getBody().asString(), Matchers.not(Matchers.equalTo("The requested resource was not found.")));
    }

    private void writeOnLog(String message){
        logger.log(Level.INFO, "\n"+message);
    }

}
