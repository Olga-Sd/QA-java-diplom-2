package config;
import io.restassured.http.Header;

public class UserData {
    public static final Header CREATE_USER_REQUEST_HEADER = new Header("Content-type", "application/json");
    public static final String USER_EMAIL = "KosmoDog@yandex.ru";
    public static final String USER_PASSWORD = "1234567";
    public static final String USER_NAME = "Belka";
}
