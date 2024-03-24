import config.*;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import com.google.gson.Gson;
import java.util.List;

// Данный класс тестирует эндпойнт "POST  /api/orders" (создание заказа).
// В рамках дипломного задания выполняются следующие проверки создания заказа:
//      -с авторизацией,
//      -без авторизации,
//      -с ингредиентами,
//      -без ингредиентов,
//      -с неверным хешем ингредиентов.

public class TestCreateOrder {
    User user;
    String token;
    Order order;

    @Before  // Задаем базовый URI
    public void init() {
        RestAssured.baseURI = Configuration.URL_STELLAR_BURGERS;
        user = new User();
    }

    // Данная проверка покрывает 2 ситуации из требований: создание заказа авторизованным пользователем и создание заказа с ингредиентами
    @Test
    @DisplayName("New order can be created by authorized user and can create orders with ingredients")
    public void testCanCreateOrderByAuthorizedUser(){
        Gson gson = new Gson();
        List<String> ingredients;
        UserAPI.createUser(user);
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        token = responseLogin.path("accessToken");
        ingredients = OrderAPI.getIngredientsID();
        order = new Order(List.of(ingredients.get(0),ingredients.get(2),ingredients.get(4)));
        String json = gson.toJson(order);
        Response responseCreateOrder = OrderAPI.createOrder(json, token);
        responseCreateOrder.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true) );
    }

    @Test
    @DisplayName("New order can be created by unauthorized user")
    public void testCanCreateOrderByUnauthorizedUser(){
        // Работа API не полностью соответствует документации: в данном случае (создание заказа неавторизованным пользователем)
        // было бы логично, чтобы заказ не создавался и приходил ответ 401. Но в реальности заказ создать можно.
        // После обсуждения с наставником решили этот тест сделать по факту работы API, т к одно из условий
        // сдачи Диплома - рабочие тесты (см. Задание 2, раздел "Как будут оценивать твою работу" пункт 2: "Тесты запускаются и проходят.")
        Gson gson = new Gson();
        UserAPI.createUser(user);
        token = "";
        List<String> ingredients = OrderAPI.getIngredientsID();
        order = new Order(List.of(ingredients.get(0),ingredients.get(2),ingredients.get(4)));
        String json = gson.toJson(order);
        Response responseCreateOrder = OrderAPI.createOrder(json, token);
        responseCreateOrder.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("New order can not be created without ingredients")
    public void testCanNotCreateOrderWithoutIngredients(){
        Gson gson = new Gson();
        UserAPI.createUser(user);
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        token = responseLogin.path("accessToken");
        order = new Order(List.of());
        String json = gson.toJson(order);
        Response responseCreateOrder = OrderAPI.createOrder(json, token);
        responseCreateOrder.then().assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }
    @Test
    @DisplayName("New order can not be created with wrong ingredients hash")
    public void testCanNotCreateOrderWithWrongIngredientsHash(){
        Gson gson = new Gson();
        UserAPI.createUser(user);
        String wrongHash = "aaaaac5a71d1f82001bdaaa6f";
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        token = responseLogin.path("accessToken");
        List<String> ingredients = OrderAPI.getIngredientsID();
        order = new Order(List.of(ingredients.get(0),ingredients.get(2),ingredients.get(4), wrongHash));
        String json = gson.toJson(order);
        Response responseCreateOrder = OrderAPI.createOrder(json, token);
        responseCreateOrder.then().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
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
