import config.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;

import static org.apache.http.HttpStatus.*;


// Данный класс тестирует эндпойнт "POST  /api/auth/register" (создание пользователя).
// В рамках дипломного задания выполняются следующие проверки:
//      -создать уникального пользователя;
//      -создать пользователя, который уже зарегистрирован;
//      -создать пользователя и не заполнить одно из обязательных полей
public class TestCreateUser {
    User user;
    String token;

    @Before  // Задаем базовый URI и создаем экземпляр класса User
    public void init() {
        RestAssured.baseURI = Configuration.URL_STELLAR_BURGERS;
        user = new User();
    }

    @Test
    @DisplayName("New user can be created")
    public void testCanCreateNewUser() {
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Can not register a user which is already created")
    public void testCanNotCreateNewUserIfUserAlreadyExists() {
        UserAPI.createUser(user);
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Can not create user without email")
    public void testCanNotCreateUserWithoutEmail() {
        String emailKeeper = user.getEmail();
        user.setEmail("");
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
        user.setEmail(emailKeeper);
    }

    @Test
    @DisplayName("Can not create user without password")
    public void testCanNotCreateUserWithoutPassword() {
        String pwdKeeper = user.getPassword();
        user.setPassword("");
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
        user.setPassword(pwdKeeper);
    }

    @Test
    @DisplayName("Can not create user without name")
    public void testCanNotCreateUserWithoutName() {
        String nameKeeper = user.getName();
        user.setName("");
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
        user.setName(nameKeeper);
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

