package utils;

import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;

public class JsonUtils {

    public static String getJsonValueArray(Response response, String elementValue, String key, String toFind){
        String jsonString = response.getBody().asString();
        String value;
        int length =  JsonPath.read(jsonString,"$.length()");
        for (int i=0; i< length; i++ ){
            String element = JsonPath.read(jsonString, "$.["+i+"]."+key);
            if(element.equals(elementValue)){
                value = JsonPath.read(jsonString, "$.["+i+"]."+toFind);
                return value;
            }
        }
        return null;
    }

    public static String getJsonValue(Response response, String key){
        String jsonString = response.getBody().asString();
        String value;
        value = JsonPath.read(jsonString, "$."+key);
        return value;
    }

}
