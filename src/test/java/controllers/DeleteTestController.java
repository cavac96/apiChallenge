package controllers;

import builders.URLBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.PropertiesReader;

import java.net.URL;

public class DeleteTestController extends  TestController{

    private static DeleteTestController deleteTestControllerInstance;

    private DeleteTestController(){}

    public static DeleteTestController DeleteTestControllerInstance(){
        if(deleteTestControllerInstance == null)
            deleteTestControllerInstance = new DeleteTestController();
        return deleteTestControllerInstance;
    }

    public URL deleteCardURL(String cardId) {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.cards"))
                .addPathStep(cardId)
                .build();
    }

    public Response deleteCard(RequestSpecification requestSpecification, String cardId){
        return requestSpecification.and().delete(deleteCardURL(cardId));
    }

    public RequestSpecification deleteCardRequestSpecification(RequestSpecification requestSpecification, String cardName, String listName){
        requestSpecification = PostTestController.PostTestControllerInstance().newVerifyListId(requestSpecification, listName);
        requestSpecification = PostTestController.PostTestControllerInstance().verifyCardId(requestSpecification, cardName, listName);
        return requestSpecification;
    }

}
