Feature: Bookstore API CRUD and Auth
  As a user of the Bookstore API
  I want to ensure all endpoints work as expected
  So that the system is robust and reliable

  Scenario: Health check
    When I send a GET request to "/health"
    Then the response code should be 200
    And the response should contain "status" with value "up"

  Scenario: User signup
    When I sign up with email "testuser@example.com" and password "testpass123"
    Then the response code should be 200
    And the response should contain "message" with value "User created successfully"

  Scenario: Duplicate user signup
    When I sign up with email "testuser@example.com" and password "testpass123"
    Then the response code should be 400
    And the response should contain "detail" with value "Email already registered"

  Scenario: User login
    When I login with email "user2@example.com" and password "yourpassword"
    Then the response code should be 200
    And the response should contain "access_token"

  Scenario: Login with wrong password
    When I login with email "testuser@example.com" and password "wrongpass"
    Then the response code should be 400
    And the response should contain "detail" with value "Incorrect email or password"

  Scenario: Book CRUD operations
    Given I am logged in as "testuser@example.com" with password "testpass123"
    When I create a book with title "Test Book" and author "Author1"
    Then the response code should be 200
    And the response should contain "title" with value "Test Book"
    When I get the created book by id
    Then the response code should be 200
    And the response should contain "title" with value "Test Book"
    When I update the book title to "Updated Book"
    Then the response code should be 200
    And the response should contain "title" with value "Updated Book"
    When I delete the book
    Then the response code should be 200
    And the response should contain "message" with value "Book deleted successfully"
    When I get the deleted book by id
    Then the response code should be 404
    And the response should contain "detail" with value "Book not found"
