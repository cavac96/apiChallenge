package controllers;

import builders.URLBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.Constants;
import utils.PropertiesReader;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostTestController extends TestController{

    private Logger logger = Logger.getLogger("PostTestController.class");
    private static PostTestController postTestControllerInstance;

    private  PostTestController(){}

    public static PostTestController PostTestControllerInstance(){
        if(postTestControllerInstance == null)
            postTestControllerInstance = new PostTestController();
        return postTestControllerInstance;
    }

    public URL postCardURL() {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.cards"))
                .build();
    }

    public URL postListURL() {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.lists"))
                .build();
    }

    public URL postCommentURL(String cardId) {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.cards"))
                .addPathStep(cardId)
                .addPathStep(PropertiesReader.getValueByKey("trello.url.actions"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.comments"))
                .build();
    }

    public URL postMemberURL(String memberId) {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.cards"))
                .addPathStep(memberId)
                .addPathStep(PropertiesReader.getValueByKey("trello.url.idMembers"))
                .build();
    }

    public Map<String, String> addListParams(String boardId, String listName){
        Map<String, String> params = new HashMap<>();
        params.put("name", listName);
        params.put("idBoard", boardId);
        return params;
    }

    public Map<String, String> addCardParams(String listId, String cardName){
        Map<String, String> params = new HashMap<>();
        params.put("name", cardName);
        params.put("idList", listId);
        return params;
    }

    public Map<String, String> addCommentParams(String comment){
        Map<String, String> params = new HashMap<>();
        params.put("text", comment);
        return params;
    }

    public Map<String, String> addMemberParams(String memberId){
        writeOnLog("memberId: "+ memberId);
        Map<String, String> params = new HashMap<>();
        params.put("value", memberId);
        return params;
    }

    public RequestSpecification postListAuthenticate(String boardId, String listName){
        Map<String, String> postPrams = addListParams(boardId, listName);
        Map<String, String> authPrams = getAuthParams();
        postPrams.putAll(authPrams);
        return RestAssured.given().contentType(ContentType.JSON)
                .and().queryParams(postPrams);
    }

    public RequestSpecification postCardAuthenticate(String listId, String cardName){
        Map<String, String> postPrams = addCardParams(listId, cardName);
        Map<String, String> authPrams = getAuthParams();
        postPrams.putAll(authPrams);
        return RestAssured.given().contentType(ContentType.JSON)
                .and().queryParams(postPrams);
    }

    public RequestSpecification postCommentAuthenticate(String comment){
        Map<String, String> postPrams = addCommentParams(comment);
        Map<String, String> authPrams = getAuthParams();
        postPrams.putAll(authPrams);
        return RestAssured.given().contentType(ContentType.JSON)
                .and().queryParams(postPrams);
    }

    public RequestSpecification postMemberAuthenticate(String memberId){
        Map<String, String> postPrams = addMemberParams(memberId);
        Map<String, String> authPrams = getAuthParams();
        postPrams.putAll(authPrams);
        return RestAssured.given().contentType(ContentType.JSON)
                .and().queryParams(postPrams);
    }

    public Response postList(RequestSpecification requestSpecification){
        return requestSpecification.and().post(postListURL());
    }

    public Response postCard(RequestSpecification requestSpecification){
        return requestSpecification.and().post(postCardURL());
    }

    public Response postComment(RequestSpecification requestSpecification, String listName, String cardName){
        String cardId = GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName);
        return requestSpecification.and().post(postCommentURL(cardId));
    }

    public Response postMember(RequestSpecification requestSpecification, String listName, String cardName){
        String cardId = GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName);
        return requestSpecification.and().post(postMemberURL(cardId));
    }

    public RequestSpecification verifyListId(RequestSpecification requestSpecification, String listId, String listName){
        if(listId == null){
            String boardId = GetTestController.GetTestControllerInstance().getBoardId(requestSpecification, Constants.BOARD);
            requestSpecification = postListAuthenticate(boardId, listName);
            postList(requestSpecification);
        }
        return requestSpecification;
    }

    public RequestSpecification newVerifyListId(RequestSpecification requestSpecification, String listName){
        String listId = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName);
        if(listId == null){
            String boardId = GetTestController.GetTestControllerInstance().getBoardId(requestSpecification, Constants.BOARD);
            requestSpecification = postListAuthenticate(boardId, listName);
            postList(requestSpecification);
        }
        return requestSpecification;
    }

    public RequestSpecification verifyCardId(RequestSpecification requestSpecification, String cardName, String listName){
        String cardId = GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName);
        if(cardId == null){
            String listId = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName);
            requestSpecification = postCardAuthenticate(listId, cardName);
            postCard(requestSpecification);
        }
        return requestSpecification;
    }

    public RequestSpecification postListRequestSpecification(RequestSpecification requestSpecification, String boardName, String listName){
        String boardId = GetTestController.GetTestControllerInstance().getBoardId(requestSpecification, boardName);
        requestSpecification = postListAuthenticate(boardId, listName);
        return requestSpecification;
    }

    public RequestSpecification postCardRequestSpecification(RequestSpecification requestSpecification, String listName, String cardName){
        requestSpecification = newVerifyListId(requestSpecification, listName);
        String listId = GetTestController.GetTestControllerInstance().getListId(requestSpecification, listName);
        requestSpecification = postCardAuthenticate(listId, cardName);
        return requestSpecification;
    }

    public RequestSpecification postCommentRequestSpecification(RequestSpecification requestSpecification, String listName, String cardName, String comment){
        requestSpecification = newVerifyListId(requestSpecification, listName);
        requestSpecification = verifyCardId(requestSpecification, cardName, listName);
        requestSpecification = postCommentAuthenticate(comment);
        return requestSpecification;
    }

    public RequestSpecification postMemberRequestSpecification(RequestSpecification requestSpecification, String boardName, String listName, String cardName){
        requestSpecification = newVerifyListId(requestSpecification, listName);
        requestSpecification = verifyCardId(requestSpecification, cardName, listName);

        String boardId = GetTestController.GetTestControllerInstance().getBoardId(requestSpecification, boardName);
        String cardId = GetTestController.GetTestControllerInstance().getCardId(requestSpecification, cardName, listName);
        String memberId = GetTestController.GetTestControllerInstance().getRandomMember(requestSpecification, boardId, cardId);

        requestSpecification = postMemberAuthenticate(memberId);
        return requestSpecification;
    }

    private void writeOnLog(String message){
        logger.log(Level.INFO, "\n"+message);
    }

}
