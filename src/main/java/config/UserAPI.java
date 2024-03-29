package config;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserAPI {
    public static final String newUserAPIPath = "/api/auth/register";
    public static final String loginUserAPIPath = "/api/auth/login";
    public static final String logoutUserAPIPath = "/api/auth/logout";
    public static final String updateUserAPIPath = "/api/auth/user"; // + PATCH
    public static final String deleteUserAPIPath = "/api/auth/user"; // + DELETE


    @Step("Create new user")
    public static Response createUser(User user) {
        Response responseCreate = given()
                .header(UserData.REQUEST_HEADER)
                .and()
                .body(user)
                .when()
                .post(newUserAPIPath);
        return responseCreate;
    }

    @Step("Login user and get authToken in response")
    public static Response loginUserAndGetToken(User user) {
        Response responseLogin = given()
                .header(UserData.REQUEST_HEADER)
                .and()
                .body(user)
                .when()
                .post(loginUserAPIPath);
        return responseLogin;
    }

    @Step("Update user's data")
    public static Response updateUser(User user, String token) {
        Response responseUpdate = given()
                .header("Authorization",token)
                .contentType("application/json")
                .body(user)
                .when()
                .patch(updateUserAPIPath);
        return responseUpdate;
    }

    @Step("Log out user")
    public static Response logoutUser(User user, String token) {
        Response responseLogout = given()
                .header("Authorization",token)
                .contentType("application/json")
                .body(user)
                .when()
                .post(logoutUserAPIPath);
        return responseLogout;
    }

    @Step("Delete user")
    public static void deleteUser(User user, String token) {
        Response responseDelete = given()
                .header("Authorization",token)
                .contentType("application/json")
                .body(user)
                .when()
                .delete(deleteUserAPIPath);
    }
}
