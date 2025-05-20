package bookstore.stepdefs;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import java.util.HashMap;
import java.util.Map;

public class BookstoreApiSteps {
    private Response response;
    private String accessToken;
    private int createdBookId;

    static {
        RestAssured.baseURI = "http://localhost:8000";
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        System.out.println("[STEP] Sending GET request to: " + endpoint);
        response = RestAssured.get(endpoint);
    }

    @Then("the response code should be {int}")
    public void the_response_code_should_be(int code) {
        System.out.println("[STEP] Asserting response code: expected=" + code + ", actual=" + response.getStatusCode());
        if (response.getStatusCode() != code) {
            System.out.println("[ERROR] Response body: " + response.asString());
        }
        Assert.assertEquals(response.getStatusCode(), code, "Expected status code " + code + " but got " + response.getStatusCode() + ". Response: " + response.asString());
    }

    @And("the response should contain {string} with value {string}")
    public void the_response_should_contain_with_value(String key, String value) {
        System.out.println("[STEP] Asserting response contains key '" + key + "' with value '" + value + "'");
        String actual = response.jsonPath().getString(key);
        if (!value.equals(actual)) {
            System.out.println("[ERROR] Response body: " + response.asString());
        }
        Assert.assertEquals(actual, value, "Expected key '" + key + "' to have value '" + value + "' but got '" + actual + "'. Response: " + response.asString());
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String key) {
        System.out.println("[STEP] Asserting response contains key '" + key + "'");
        Assert.assertNotNull(response.jsonPath().getString(key), "Response does not contain key: " + key);
    }

    @When("I sign up with email {string} and password {string}")
    public void i_sign_up_with_email_and_password(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        System.out.println("[STEP] Signing up with email: " + email);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given().contentType("application/json").body(body).post("/signup");
    }

    @When("I login with email {string} and password {string}")
    public void i_login_with_email_and_password(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        System.out.println("[STEP] Logging in with email: " + email);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given().contentType("application/json").body(body).post("/login");
        if (response.getStatusCode() == 200) {
            accessToken = response.jsonPath().getString("access_token");
            System.out.println("[STEP] Received access token: " + accessToken);
        }
    }

    @Given("I am logged in as {string} with password {string}")
    public void i_am_logged_in_as_with_password(String email, String password) {
        // Always sign up the user before login to ensure the user exists
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        System.out.println("[STEP] (Auto) Signing up with email: " + email);
        RestAssured.given().contentType("application/json").body(body).post("/signup");
        System.out.println("[STEP] Logging in as: " + email);
        i_login_with_email_and_password(email, password);
        Assert.assertNotNull(accessToken);
    }

    @When("I create a book with title {string} and author {string}")
    public void i_create_a_book_with_title_and_author(String title, String author) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("author", author);
        System.out.println("[STEP] Creating book with title: " + title + ", author: " + author);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(body)
                .post("/books/");
        if (response.getStatusCode() == 200) {
            createdBookId = response.jsonPath().getInt("id");
            System.out.println("[STEP] Created book with id: " + createdBookId);
        }
    }

    @When("I get the created book by id")
    public void i_get_the_created_book_by_id() {
        System.out.println("[STEP] Getting book by id: " + createdBookId);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .get("/books/" + createdBookId);
    }

    @When("I update the book title to {string}")
    public void i_update_the_book_title_to(String newTitle) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", newTitle);
        System.out.println("[STEP] Updating book id " + createdBookId + " title to: " + newTitle);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(body)
                .put("/books/" + createdBookId);
    }

    @When("I delete the book")
    public void i_delete_the_book() {
        System.out.println("[STEP] Deleting book id: " + createdBookId);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .delete("/books/" + createdBookId);
    }

    @When("I get the deleted book by id")
    public void i_get_the_deleted_book_by_id() {
        System.out.println("[STEP] Getting (deleted) book by id: " + createdBookId);
        i_get_the_created_book_by_id();
    }
}
