package config;

import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserAPI {
    public static final String newUserAPIPath = "/api/auth/register";
    public static final String loginUserAPIPath = "/api/auth/login";
    public static final String updateUserAPIPath = "/api/auth/user"; // + PATCH
    public static final String deleteUserAPIPath = "/api/auth/user"; // + DELETE


    @Step("Create new user")
    public static Response createUser(User user) {
        Response responseCreate = given()
                .header(UserData.CREATE_USER_REQUEST_HEADER)
                .and()
                .body(user)
                .when()
                .post(newUserAPIPath);
        return responseCreate;
    }

    @Step("Login user and get authToken")
    public static Response loginUserAndGetToken(User user) {
        Response responseLogin = given()
                .header(UserData.CREATE_USER_REQUEST_HEADER)
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

    @Step("Delete user")
    public static void deleteUser(User user, String token) {
        Response responseDelete = given()
                .header("Authorization",token)
                .contentType("application/json")
                .body(user)
                .when()
                .delete(deleteUserAPIPath);
        //responseDelete.getBody().prettyPrint();
    }


}
