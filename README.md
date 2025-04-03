# Walled Project README

This document provides instructions on how to run the Walled project using `./mvnw` and details the API testing documentation based on the provided Postman collection.

## Prerequisites

* Java Development Kit (JDK) 11 or higher
* Maven Wrapper (`./mvnw`) included in the project

## Running the Project

1.  **Clone the Repository (if you haven't already):**

    ```bash
    git clone git@github.com:verlym/walled-api.git
    cd walled-api
    ```

2.  **Build and Run the Application:**

    Use the Maven Wrapper to build and run the Spring Boot application.

    ```bash
    ./mvnw spring-boot:run
    ```

    This command will download the necessary dependencies, compile the code, and start the application. The application will be accessible at `http://localhost:8080`.

## API Testing Documentation

The following API endpoints are defined in the Postman collection.

### User Management

1.  **Create User1:**

    * **Endpoint:** `POST http://localhost:8080/api/users/register`
    * **Request Body (JSON):**

        ```json
        {
            "email": "john.doe@example.com",
            "username": "johndoe",
            "fullname": "John Doe",
            "password": "password123"
        }
        ```

    * **Description:** Registers a new user.

2.  **Login:**

    * **Endpoint:** `POST http://localhost:8080/api/auth/login`
    * **Request Body (JSON):**

        ```json
        {
            "email": "test@example.com",
            "password": "test123"
        }
        ```

    * **Description:** Logs in an existing user.
    * **Note:** The provided json uses `test@example.com` which is different from the user created in the step 1. Make sure to use the correct email and password.

### Wallet Management

3.  **Create Wallet for User1:**

    * **Endpoint:** `POST http://localhost:8080/api/users/john.doe@example.com/wallets`
    * **Description:** Creates a wallet for the specified user.

4.  **Top-Up Wallet1:**

    * **Endpoint:** `POST http://localhost:8080/api/wallets/123000001/top-up?amount=100`
    * **Header:** `User-Email: john.doe@example.com`
    * **Query Parameter:** `amount=100`
    * **Description:** Tops up the specified wallet with the given amount.
    * **Note:** Make sure the walletID `123000001` exists.

5.  **Check Wallet1 Balance:**

    * **Endpoint:** `GET` (Endpoint details are not present in provided data).
    * **Description:** Gets the balance of Wallet1.
    * **Note:** The endpoint is missing from the provided data. You will need to determine the correct endpoint from the application's API documentation or code.

6.  **Check Wallet1 Transactions:**

    * **Endpoint:** `GET` (Endpoint details are not present in provided data).
    * **Description:** Retrieves the transaction history of Wallet1.
    * **Note:** The endpoint is missing from the provided data. You will need to determine the correct endpoint from the application's API documentation or code.

7.  **Transfer from Wallet1 to Wallet2:**

    * **Endpoint:** `GET` (Endpoint details are not present in provided data).
    * **Description:** Transfers funds from Wallet1 to Wallet2.
    * **Note:** The endpoint is missing from the provided data. You will need to determine the correct endpoint from the application's API documentation or code.

8.  **Check Wallet2 Transactions:**

    * **Endpoint:** `GET` (Endpoint details are not present in provided data).
    * **Description:** Retrieves the transaction history of Wallet2.
    * **Note:** The endpoint is missing from the provided data. You will need to determine the correct endpoint from the application's API documentation or code.

9.  **Check Wallet2 Balance:**

    * **Endpoint:** `GET http://localhost:8080/api/wallets/2`
    * **Header:** `Content-Type: application/json`
    * **Description:** Gets the balance of Wallet2.

10. **Logout:**

    * **Endpoint:** `GET http://localhost:8080/api/wallets/2`
    * **Header:** `Content-Type: application/json`
    * **Description:** This endpoint is using the same endpoint of "Check Wallet2 Balance" which is probably a mistake. The real logout endpoint is not provided in the data. You will need to determine the correct logout endpoint from the application's API documentation or code.

    **Note:** The logout endpoint in the provided data is incorrect. It shares the same endpoint as "Check Wallet2 Balance." The real logout endpoint needs to be determined from the application's API documentation or code.