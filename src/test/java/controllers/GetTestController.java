package controllers;

import builders.URLBuilder;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.Constants;
import utils.PropertiesReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetTestController extends TestController{

    private static GetTestController getTestControllerInstance;
    private Logger logger = Logger.getLogger("GetTestController.class");

    private GetTestController(){}

    public static GetTestController GetTestControllerInstance() {
        if(getTestControllerInstance == null)
            getTestControllerInstance = new GetTestController();
        return getTestControllerInstance;
    }

    public URL getBoardsURL(String userId) {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.members"))
                .addPathStep(userId)
                .addPathStep(PropertiesReader.getValueByKey("trello.url.boards"))
                .build();
    }

    public URL getListsURL(String boardId){
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.boards"))
                .addPathStep(boardId)
                .addPathStep(PropertiesReader.getValueByKey("trello.url.lists"))
                .build();
    }

    public URL getCardsURL(String listId) {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.lists"))
                .addPathStep(listId)
                .addPathStep(PropertiesReader.getValueByKey("trello.url.cards"))
                .build();
    }

    public URL getBoardMembersURL(String boardId) {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.boards"))
                .addPathStep(boardId)
                .addPathStep(PropertiesReader.getValueByKey("trello.url.members"))
                .build();
    }

    public URL getCardMembersURL(String cardId) {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.cards"))
                .addPathStep(cardId)
                .addPathStep(PropertiesReader.getValueByKey("trello.url.members"))
                .build();
    }

    public Response getBoards(RequestSpecification requestSpecification, String userId){
        return requestSpecification.and().get(this.getBoardsURL(userId));
    }

    public String getBoardId(RequestSpecification requestSpecification, String boardName){
        Response response = getBoards(requestSpecification, getUserId(requestSpecification));
        String sJson = response.getBody().asString();
        int length =  JsonPath.read(sJson,"$.length()");
        for (int i=0; i< length; i++ ){
            String name = JsonPath.read(sJson, "$.["+i+"].name");
            if(name.equals(boardName)){
                String boardId = JsonPath.read(sJson, "$.["+i+"].id");
                return boardId;
            }
        }
        writeOnLog("There are not boards with that name: " + boardName);
        return null;
    }

    public Response getListsOfBoard(RequestSpecification requestSpecification, String boardId){
        return requestSpecification.and().get(this.getListsURL(boardId));
    }

    public String getListId(RequestSpecification requestSpecification, String listName){
        String boardId = getBoardId(requestSpecification, Constants.BOARD);
        Response response = getListsOfBoard(requestSpecification, boardId);
        String sJson = response.getBody().asString();
        int length =  JsonPath.read(sJson,"$.length()");
        for (int i=0; i< length; i++ ){
            String name = JsonPath.read(sJson, "$.["+i+"].name");
            if(name.equals(listName)){
                String listId = JsonPath.read(sJson, "$.["+i+"].id");
                return listId;
            }
        }
       writeOnLog("There are not lists with that name: " + listName);
        return null;
    }

    public Response getCards(RequestSpecification requestSpecification, String listId){
        return requestSpecification.and().get(this.getCardsURL(listId));
    }

    public String getCardId(RequestSpecification requestSpecification, String cardName, String listName){
        String listId = getListId(requestSpecification, listName);
        Response response = getCards(requestSpecification, listId);
        String sJson = response.getBody().asString();
        int length =  JsonPath.read(sJson,"$.length()");
        for (int i=0; i< length; i++ ){
            String name = JsonPath.read(sJson, "$.["+i+"].name");
            if(name.equals(cardName)){
                String cardId = JsonPath.read(sJson, "$.["+i+"].id");
                return cardId;
            }
        }
        writeOnLog("There are not cards with that name: " + cardName);
        return null;
    }

    public Response getBoardMembers(RequestSpecification requestSpecification, String boardId){
        return requestSpecification.and().get(this.getBoardMembersURL(boardId));

    }

    public Response getCardMembers(RequestSpecification requestSpecification, String cardId){
        return requestSpecification.and().get(this.getCardMembersURL(cardId));
    }

    public String getRandomMember(RequestSpecification requestSpecification, String boardId, String cardId){
        Response responseBoard = getBoardMembers(requestSpecification, boardId);
        Response responseCard = getCardMembers(requestSpecification, cardId);

        String boardJson = responseBoard.getBody().asString();
        List<String> memberBoardIdList = new ArrayList<>();
        int length =  JsonPath.read(boardJson,"$.length()");
        for (int i=0; i< length; i++ ){
            String boardMemberId = JsonPath.read(boardJson, "$.["+i+"].id");
            memberBoardIdList.add(boardMemberId);
        }
        if(memberBoardIdList.isEmpty())
            writeOnLog("There are not members in the board: " + Constants.BOARD);
        else{
            String cardJson = responseCard.getBody().asString();
            List<String> memberCardIdList = new ArrayList<>();
            length =  JsonPath.read(cardJson,"$.length()");
            for (int i=0; i< length; i++ ){
                String cardMemberId = JsonPath.read(boardJson, "$.["+i+"].id");
                memberCardIdList.add(cardMemberId);
            }
            if (memberCardIdList.isEmpty())
                return memberBoardIdList.get(0);
            else {
                for (String bMember : memberBoardIdList) {
                    if (!memberCardIdList.contains(bMember))
                        return bMember;
                }
            }
        }
        writeOnLog("There are not members to add. Board: " + Constants.BOARD);
        return null;
    }

    private  void writeOnLog(String message){
        logger.log(Level.WARNING, "\n" + message);
    }
}
