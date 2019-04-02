package steps;

import controllers.GetTestController;
import controllers.PutTestController;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.annotations.Steps;
import org.hamcrest.Matchers;
import org.junit.Assert;
import utils.JsonUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PutTrelloSteps {

    private Logger logger = Logger.getLogger("PutTrelloSteps.class");
    @Steps
    private PutTestController testControllerInstance = PutTestController.PutTestControllerInstance();
    private RequestSpecification requestSpecification;
    private Response response;

    String listName1;
    String cardName;
    String listName2;

    @Given("^the user wants to move the card \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void theUserWantsToMoveTheCardFromTo(String cardName, String listName1, String listName2) {
        this.cardName = cardName;
        this.listName2 = listName2;
        this.listName1 = listName1;

        requestSpecification = testControllerInstance.authenticate();
        requestSpecification = testControllerInstance.moveCardRequestSpecification(requestSpecification, cardName, listName1, listName2);
    }

    @When("^the user sends the put request to move the card$")
    public void theUserSendsThePutRequestToMoveTheCard() {
        response = testControllerInstance.moveCard(requestSpecification, cardName, listName1);
    }

    @Then("^the Trello API should response moving the card$")
    public void theTrelloAPIShouldResponseMovingTheCard() {
        String listId1 = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName1);
        String listId2 = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName2);

        writeOnLog("moved card > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: The card was not moved to the right list.", JsonUtils.getJsonValue(response,"idList"), Matchers.equalTo(listId2));
        Assert.assertThat("Error: The card was not moved.", JsonUtils.getJsonValue(response,"idList"), Matchers.not(Matchers.equalTo(listId1)));
        Assert.assertThat("Error: The card was not found.", response.getBody().asString(), Matchers.not(Matchers.equalTo("The requested resource was not found.")));
    }

    private void writeOnLog(String message){
        logger.log(Level.INFO, "\n"+message);
    }
}