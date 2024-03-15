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

// Данный класс тестирует эндпойнт "POST  /api/auth/login" (логин пользователя).
// В рамках дипломного задания выполняются следующие проверки:
//      -логин под существующим пользователем,
//      -логин с неверным логином и паролем.

public class TestLoginUser {
    User user;
    String token;

    @Before  // Задаем базовый URI и создаем экземпляр класса User
    public void createCourierInit() {
        RestAssured.baseURI = Configuration.URL_STELLAR_BURGERS;
        user = new User();
    }

    @Test
    @DisplayName("An existing user can be logged in")
    public void testCanLoginExistingUser() {
        UserAPI.createUser(user).then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        responseLogin.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }


    @Test
    @DisplayName("User can not login with wrong Login(in this app email is Login)")
    public void testCanNotLoginWithWrongLogin() {
        UserAPI.createUser(user).then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        String emailKeeper = user.getEmail();
        user.setEmail("wrongEmail@yandex.ru");
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        responseLogin.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
        user.setEmail(emailKeeper);

    }

    @Test
    @DisplayName("User can not login with wrong Password")
    public void testCanNotLoginWithWrongPassword() {
        UserAPI.createUser(user).then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        String pwdKeeper = user.getPassword();
        user.setPassword("wrongPwd");
        Response responseLogin = UserAPI.loginUserAndGetToken(user);
        responseLogin.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
        user.setPassword(pwdKeeper);

    }

    @After
    @Description("Deletion of a user if exists")
    public void deleteTestUserIfExists() {

        try {
            Response responseLogin = UserAPI.loginUserAndGetToken(user);
            if (responseLogin.path("success").equals(true)) {
                token = responseLogin.path("accessToken");
                //System.out.println(responseLogin.getBody().asString());
                UserAPI.deleteUser(user, token);
            }


        } catch (NullPointerException e) {
        }

    }

}
