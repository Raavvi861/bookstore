Feature: Bookstore API CRUD and Auth
  As a user of the Bookstore API
  I want to ensure all endpoints work as expected
  So that the system is robust and reliable

  Scenario: Health check
    When I send a GET request to "/health"
    Then the response code should be 200
    And the response should contain "status" with value "up"

  Scenario: User signup with unique email
  # Use a unique random email for this scenario to ensure it always passes
    When I sign up with email "RANDOM_EMAIL" and password "testpass123"
    Then the response code should be 200
    And the response should contain "message" with value "User created successfully"

  Scenario: Duplicate user signup with same email
    When I sign up with email "RANDOM_EMAIL" and password "testpass123"
    Then the response code should be 400
    And the response should contain "detail" with value "Email already registered"

  Scenario: User login with correct credentials
    # Use a unique random email for this scenario to ensure it always passes
    When I sign up with email "RANDOM_EMAIL" and password "testpass123"
    Then the response code should be 200
    And the response should contain "message" with value "User created successfully"
    When I login with email "RANDOM_EMAIL" and password "testpass123"
    Then the response code should be 200
    And the response should contain "access_token"

  Scenario: User login with wrong password
    When I login with email "RANDOM_EMAIL" and password "wrongpass"
    Then the response code should be 400
    And the response should contain "detail" with value "Incorrect email or password"

  Scenario: User login with missing email
    When I login with email "" and password "testpass123"
    Then the response code should be 400
    And the response should contain "detail" with value "Incorrect email or password"

  Scenario: User login with missing password
    When I login with email "RANDOM_EMAIL" and password ""
    Then the response code should be 400
    And the response should contain "detail" with value "Incorrect email or password"

  Scenario: Book CRUD operations with valid token
    Given I am logged in as "RANDOM_EMAIL" with password "testpass123"
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

  Scenario: Book creation without token
    When I create a book with title "NoToken Book" and author "NoAuth"
    Then the response code should be 403
    And the response should contain "detail" with value "Invalid token or expired token"
