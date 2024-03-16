package config;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderAPI {

    public static final String newOrderAPIPath = "/api/orders";   // POST !!
    public static final String getUserOrdersAPIPath = "/api/orders";  // GET !!

    @Step("Create new order")
    public static Response createOrder(String json, String token) {
        //System.out.println(order.getIngredientsHashes());
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
        //System.out.println(order.getIngredientsHashes());
        Response responseGetUserOrders = given()
                .header("Authorization",token)
                .contentType("application/json")
                .when()
                .get(getUserOrdersAPIPath);
        return responseGetUserOrders;
    }
}
