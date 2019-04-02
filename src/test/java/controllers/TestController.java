package controllers;

import builders.URLBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.steps.ScenarioSteps;
import utils.PropertiesReader;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TestController extends ScenarioSteps {

    private JsonPath jsonPath;

    public Map<String, String> getAuthParams(){
        Map<String, String> params = new HashMap<>();
        params.put("key", PropertiesReader.getValueByKey("trello.key"));
        params.put("token", PropertiesReader.getValueByKey("trello.token"));
        return params;
    }

    public URL getUserIdURL() {
        return new URLBuilder().addDomain(PropertiesReader.getValueByKey("trello.url"))
                .addPathStep(PropertiesReader.getValueByKey("trello.url.tokens"))
                .addPathStep(PropertiesReader.getValueByKey("trello.token"))
                .build();
    }

    public RequestSpecification authenticate(){
        return RestAssured.given().contentType(ContentType.JSON).and().queryParams(getAuthParams());
    }

    public String getUserId(RequestSpecification requestSpecification){
        jsonPath = (requestSpecification.when().get(getUserIdURL())).jsonPath();
        return jsonPath.get("idMember");
    }

}
