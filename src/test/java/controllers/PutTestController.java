package controllers;

import builders.URLBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.PropertiesReader;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PutTestController extends TestController{

    private static PutTestController putTestControllerInstance;

    private PutTestController(){}

    public static PutTestController PutTestControllerInstance(){
        if(putTestControllerInstance == null)
            putTestControllerInstance = new PutTestController();
        return  putTestControllerInstance;
    }

    public URL moveCardURL(String cardId){
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.cards"))
                .addPathStep(cardId)
                .build();
    }

    public Map<String, String> moveCardParams(String listId){
        Map<String, String> params = new HashMap<>();
        params.put("idList", listId);
        return params;
    }

    public RequestSpecification moveCardAuthenticate(String listId){
        Map<String, String> putPrams = moveCardParams(listId);
        Map<String, String> authPrams = getAuthParams();
        putPrams.putAll(authPrams);
        return RestAssured.given().contentType(ContentType.JSON)
                .and().queryParams(putPrams);
    }

    public Response moveCard(RequestSpecification requestSpecification, String cardName, String listName1){
        String cardId = GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName1);
        return requestSpecification.and().put(moveCardURL(cardId));
    }

    public RequestSpecification moveCardRequestSpecification(RequestSpecification requestSpecification, String cardName, String listName1, String listName2){
        requestSpecification = PostTestController.PostTestControllerInstance().newVerifyListId(requestSpecification, listName1);
        requestSpecification = PostTestController.PostTestControllerInstance().newVerifyListId(requestSpecification, listName2);
        requestSpecification = PostTestController.PostTestControllerInstance().verifyCardId(requestSpecification, cardName, listName1);
        String listId2 = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName2);;
        requestSpecification = moveCardAuthenticate(listId2);
        return requestSpecification;
    }

}
