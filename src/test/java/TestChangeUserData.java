import config.*;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;

import io.restassured.RestAssured;
import org.junit.Test;

import java.util.Locale;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

// Данный класс тестирует эндпойнт "PATCH  /api/auth/user" (изменение данных пользователя).
// В рамках дипломного задания выполняются следующие проверки:
//      - изменение данных с авторизацией
//      - изменение данных без авторизации.


public class TestChangeUserData {
    User user;
    String token;

    @Before  // Задаем базовый URI и создаем экземпляр класса User
    public void createCourierInit() {
        RestAssured.baseURI = Configuration.URL_STELLAR_BURGERS;
        user = new User();
    }

    @Test
    @DisplayName("Authorized user's email can be changed")
    public void testCanChangeAuthorizedUserEmail() {
        String newEmail = "updatedEmail@yandex.ru";
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        responseLogin.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        token = responseLogin.path("accessToken");
        user.setEmail(newEmail);
        Response responseUpdate = UserAPI.updateUser(user, token);
        responseUpdate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email".toLowerCase(), equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Authorized user's name can be changed")
    public void testCanChangeAuthorizedUserName() {
        String newName = "UpdatedName";
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        responseLogin.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        token = responseLogin.path("accessToken");
        user.setName(newName);
        Response responseUpdate = UserAPI.updateUser(user, token);
        responseUpdate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Authorized user's password can be changed")
    public void testCanChangeAuthorizedUserPassword() {
// В данной проверке более сложная логика из-за того, что в ответе на запрос обновления данных
// не приходит новый пароль. Чтобы убедиться, что пароль поменялся, мы логинимся с новыми данными и убеждаемся, что
// залогиниться получилось.
        String newPassword = "updatedPwd";
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        responseLogin.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        token = responseLogin.path("accessToken");
        user.setPassword(newPassword);
        Response responseUpdate = UserAPI.updateUser(user, token);
        responseUpdate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        responseLogin = UserAPI.loginUserAndGetToken(user);
        responseLogin.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

    }

    @Test
    @DisplayName("Unauthorized user's email can not be changed")
    public void testCanNotChangeUnauthorizedUserEmail() {
        String emailKeeper = user.getEmail();
        String newEmail = "updatedEmail@yandex.ru";
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        token = "";
        user.setEmail(newEmail);
        Response responseUpdate = UserAPI.updateUser(user, token);
        responseUpdate.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
        user.setEmail(emailKeeper);
    }

    @Test
    @DisplayName("Unauthorized user's password can not be changed")
    public void testCanNotChangeUnauthorizedUserPassword() {
        String pwdKeeper = user.getPassword();
        String newPassword = "updatedPwd";
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        token = "";
        user.setPassword(newPassword);
        Response responseUpdate = UserAPI.updateUser(user, token);
        responseUpdate.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
        user.setPassword(pwdKeeper);
    }

    @Test
    @DisplayName("Unauthorized user's name can not be changed")
    public void testCanNotChangeUnauthorizedUserName() {
        String nameKeeper = user.getName();
        String newName = "updatedName";
        Response responseCreate = UserAPI.createUser(user);
        responseCreate.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        token = "";
        user.setName(newName);
        Response responseUpdate = UserAPI.updateUser(user, token);
        responseUpdate.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
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
