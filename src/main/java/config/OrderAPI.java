package config;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderAPI {

    public static final String getIngredientsAPIPath = "/api/ingredients";
    public static final String newOrderAPIPath = "/api/orders";   // POST !!
    public static final String getUserOrdersAPIPath = "/api/orders";  // GET !!

    @Step("Get an ingredient ID by name")
    public static List<String> getIngredientsID(){
        List<String> listOfIngredients = given()
                .header(UserData.REQUEST_HEADER)
                .contentType("application/json")
                .get(getIngredientsAPIPath)
                .then()
                .extract()
                .path("data._id");
        return listOfIngredients;
    }

    @Step("Create new order")
    public static Response createOrder(String json, String token) {
        Response responseCreateOrder = given()
                .header("Authorization",token)
                .contentType("application/json")
                .and()
                .body(json)
                .when()
                .post(newOrderAPIPath);
        return responseCreateOrder;
    }

    @Step("Get a specific user's orders")
    public static Response getUserOrders(String token) {
        Response responseGetUserOrders = given()
                .header("Authorization",token)
                .contentType("application/json")
                .when()
                .get(getUserOrdersAPIPath);
        return responseGetUserOrders;
    }
}
