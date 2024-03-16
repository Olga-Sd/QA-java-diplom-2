import config.*;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

import com.google.gson.Gson;

import java.util.List;

// Данный класс тестирует эндпойнт "GET  /api/orders" (получение заказов конкретного пользователя).
// В рамках дипломного задания выполняются следующие проверки получения заказов:
//      -авторизованный пользователь,
//      -неавторизованный пользователь.
public class TestGettingASpecificUserOrders {

    User user;
    String token;

    Order order;

    @Before  // Задаем базовый URI
    public void init() {
        RestAssured.baseURI = Configuration.URL_STELLAR_BURGERS;
        user = new User();
    }

    @Test
    @DisplayName("Getting a specific authorized user's orders")
    public void testGettingAuthorizedUserOrders() {
        Gson gson = new Gson();
        UserAPI.createUser(user);
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        token = responseLogin.path("accessToken");
        order = new Order(OrderTestData.orderWithIngredientsData);
        String json = gson.toJson(order);
        OrderAPI.createOrder(json, token);
        order = new Order(OrderTestData.orderWithIngredientsData2);
        json = gson.toJson(order);
        OrderAPI.createOrder(json, token);
        Response responseGetUserOrders = OrderAPI.getUserOrders(token);

        responseGetUserOrders.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("orders.name", equalTo(List.of(OrderTestData.orderName, OrderTestData.orderName2)));
    }

    @Test
    @DisplayName("Can not get a specific unauthorized user's orders")
    public void testCanNotGetUnauthorizedUserOrders() {
        // Логика проверки: создаем пользователя, логинимся,создаем 2 заказа,
        // выходим из приложения, пробуем получить заказы без токена
        Gson gson = new Gson();
        UserAPI.createUser(user);
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        token = responseLogin.path("accessToken");
        order = new Order(OrderTestData.orderWithIngredientsData);
        String json = gson.toJson(order);
        OrderAPI.createOrder(json, token);
        order = new Order(OrderTestData.orderWithIngredientsData2);
        json = gson.toJson(order);
        OrderAPI.createOrder(json, token);
        UserAPI.logoutUser(user, token);
        token = "";
        Response responseGetUserOrders = OrderAPI.getUserOrders(token);

        responseGetUserOrders.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @After
    @Description("Deletion of a user if exists")
    public void deleteTestUserIfExists() {

        try {
            Response responseLogin = UserAPI.loginUserAndGetToken(user);
            if (responseLogin.path("success").equals(true)) {
                token = responseLogin.path("accessToken");
                UserAPI.deleteUser(user, token);
            }

        } catch (NullPointerException e) {
        }

    }

}
