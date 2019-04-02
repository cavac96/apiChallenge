package steps;

import controllers.DeleteTestController;
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

import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteTrelloSteps {

    private Logger logger = Logger.getLogger("DeleteTrelloSteps.class");
    @Steps
    private DeleteTestController testControllerInstance = DeleteTestController.DeleteTestControllerInstance();
    private RequestSpecification requestSpecification;
    private Response response;

    String cardId;
    String cardName;
    String listName;

    @Given("^the user wants to delete the card \"([^\"]*)\" in \"([^\"]*)\"$")
    public void theUserWantsToDeleteTheCardIn(String cardName, String listName){
        this.cardName = cardName;
        this.listName = listName;

        requestSpecification = testControllerInstance.authenticate();
        requestSpecification = testControllerInstance.deleteCardRequestSpecification(requestSpecification, cardName, listName);
        cardId = GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName);
    }

    @When("^the user sends the request to delete the card$")
    public void theUserSendsTheRequestToDeleteTheCard() {
        response = testControllerInstance.deleteCard(requestSpecification, cardId);
    }

    @Then("^the Trello API should response with deleting the card$")
    public void theTrelloAPIShouldResponseWithDeletingTheCard() {
        writeOnLog("deleted card > "+response.getBody().asString());
        Assert.assertThat("Error: The status code is not <200>", response.getStatusCode(), Matchers.equalTo(200));
        Assert.assertThat("Error: The card was not deleted.", response.getBody().asString(), Matchers.containsString("{\"limits\":{}}"));
        Assert.assertThat("Error: The card still exists.", GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName), Matchers.isEmptyOrNullString());
    }

    private void writeOnLog(String message){
        logger.log(Level.INFO, "\n"+message);
    }
}
