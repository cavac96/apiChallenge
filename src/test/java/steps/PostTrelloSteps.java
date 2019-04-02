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
import utils.JsonUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PostTrelloSteps {

    private Logger logger = Logger.getLogger("PostTrelloSteps.class");
    @Steps
    private PostTestController testControllerInstance = PostTestController.PostTestControllerInstance();
    private RequestSpecification requestSpecification;
    private Response response;

    String userId;
    String boardId;
    String listId;
    String cardId;
    String memberId;
    String listName;
    String cardName;
    String comment;

    @Given("^the user wants to create a new list \"([^\"]*)\" in a board$")
    public void theUserWantsToCreateANewListInABoard(String listName) {
        this.listName = listName;
        requestSpecification = testControllerInstance.authenticate();
        requestSpecification = testControllerInstance.postListRequestSpecification(requestSpecification, Constants.BOARD, listName);
    }

    @When("^the user sends the request to create the list$")
    public void theUserSendsTheRequestToCreateTheList() {
        response = testControllerInstance.postList(requestSpecification);
    }

    @Then("^the Trello API should response with the new list$")
    public void theTrelloAPIShouldResponseWithTheNewList() {
        writeOnLog("new list > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: The list was not created.", JsonUtils.getJsonValue(response, "name"), Matchers.equalTo(listName));
    }

    @Given("^the user wants to create a new card \"([^\"]*)\" in the \"([^\"]*)\" list$")
    public void theUserWantsToCreateANewCardInTheList(String cardName, String listName) {
        this.cardName = cardName;
        this.listName = listName;

        requestSpecification = testControllerInstance.authenticate();
        requestSpecification = testControllerInstance.postCardRequestSpecification(requestSpecification, listName, cardName);
    }

    @When("^the user sends the request to create the card$")
    public void theUserSendsTheRequestToCreateTheCard() {
        response = testControllerInstance.postCard(requestSpecification);
    }

    @Then("^the Trello API should response with the new card$")
    public void theTrelloAPIShouldResponseWithTheNewCard() {
        String listId = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName);

        writeOnLog("new card > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: The card was not created.", JsonUtils.getJsonValue(response, "name"), Matchers.equalTo(cardName));
        Assert.assertThat("Error: The card was not created in the right list.", JsonUtils.getJsonValue(response, "idList"), Matchers.equalTo(listId));
    }

    @Given("^the user wants to create a comment \"([^\"]*)\" in a card \"([^\"]*)\" on the list \"([^\"]*)\"$")
    public void theUserWantsToCreateACommentInACardOnTheList(String comment, String cardName, String listName){
        this.cardName = cardName;
        this.listName = listName;
        this.comment = comment;

        requestSpecification = testControllerInstance.authenticate();
        requestSpecification = testControllerInstance.postCommentRequestSpecification(requestSpecification, listName, cardName, comment);
    }

    @When("^the user sends the request to create the new comment$")
    public void theUserSendsTheRequestToCreateTheNewComment() {
        response = testControllerInstance.postComment(requestSpecification, listName, cardName);
    }

    @Then("^the Trello API should response with the new comment$")
    public void theTrelloAPIShouldResponseWithTheNewComment() {
        String userId = testControllerInstance.getUserId(requestSpecification);
        String listId = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName);
        String cardId = GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName);

        writeOnLog("new comment > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: The comment was not created.", JsonUtils.getJsonValue(response, "data.text"), Matchers.equalTo(comment));
        Assert.assertThat("Error: The comment was not created in the right user.", JsonUtils.getJsonValue(response, "idMemberCreator"), Matchers.equalTo(userId));
        Assert.assertThat("Error: The comment was not created in the right list.", JsonUtils.getJsonValue(response, "data.list.id"), Matchers.equalTo(listId));
        Assert.assertThat("Error: The comment was not created in the right card.", JsonUtils.getJsonValue(response, "data.card.id"), Matchers.equalTo(cardId));
    }

    @Given("^the user wants to add a member in the card \"([^\"]*)\" on the list \"([^\"]*)\"$")
    public void theUserWantsToAddAMemberInTheCardOnTheList(String cardName, String listName){
        this.cardName = cardName;
        this.listName = listName;

        requestSpecification = testControllerInstance.authenticate();
        requestSpecification = testControllerInstance.postMemberRequestSpecification(requestSpecification, Constants.BOARD, listName, cardName);
    }

    @When("^the user sends the request to add a member in a card$")
    public void theUserSendsTheRequestToAddAMemberInACard() {
        response = testControllerInstance.postMember(requestSpecification, listName, cardName);
    }

    @Then("^the Trello API should response with the added member$")
    public void theTrelloAPIShouldResponseWithTheAddedMember() {
        writeOnLog("new member > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
       // Assert.assertThat("Error: The member was not added.", JsonUtils.getJsonValueArray(response, memberId, "id","id"), Matchers.equalTo(memberId));
        Assert.assertThat("Error: The member was already added to the card.", response.getBody().asString(), Matchers.not(Matchers.equalTo("member is already on the card")));
    }

    private void writeOnLog(String message){
        logger.log(Level.INFO, "\n"+message);
    }
}
