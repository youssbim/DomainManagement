# Distributed Systems Project 2023-2024 - REST API
Documentation of the REST APIs for the "Internet Domain Purchase and Release Management" project provided by the Web server.
Each resource (user, order, and domain) made available by the server has a dedicated path.

## `/users/{email}`

### GET

**Description**: logs in and returns the user's data.

**Parameters**: a path parameter 'email' that represents the registered user's email address.

**Request Body**: empty.

**Response**: on success, the JSON representation of the user is returned.

**Returned Status Codes**:
* 200 OK
* 400 Bad Request

## `/users/{register}`

### POST

**Description**: registers a new user in the database.

**Parameters**: the 'Content-Type: application/json' header must be present.

**Request Body**: JSON representation of the user with the following fields:
1. email
2. name
3. surname

**Response**: on success, the body is empty, and the created resource is indicated in the `Location` header.

**Returned Status Codes**:
* 201 Created: success.
* 400 Bad Request: client-side error (missing field, JSON, etc.).

## `orders/{done_by}`

### GET

**Description**: returns the list of orders made by the user who logged in.

**Parameters**: a path parameter 'done_by' that represents the user's email address.

**Request Body**: empty.

**Response**: on success, the JSON representation of the list of orders is returned.

**Returned Status Codes**:
* 200 OK: success.

## `orders/`

### POST

**Description**: records the data related to the order of an available Internet domain in the database.

**Parameters**: the 'Content-Type: application/json' header must be present.

**Request Body**: JSON representation of the order with the following fields:
1. cardCVV
2. cardExpiration
3. cardNumber
4. cardOwner
5. domain
6. duration
7. email
8. name
9. surname

**Response**: on success, the JSON representation of the domain is returned with the following fields:
1. available (can be true or false)
2. owner (complete data of the owner with a list of orders placed and registered domains)
3. registered_on (purchase date)
4. expires_on (expiration date)

**Returned Status Codes**:
* 201 Created: The Internet domain was successfully created.

## `orders/{domain}`

### GET

**Description**: returns a JSON representation of the purchase availability of the Internet domain with the following field:
1. available (can be true or false)

**Parameters**: a path parameter 'domain' that represents the domain name.

**Request Body**: empty.

**Response**: if the domain is not available, a JSON representation of the domain data is returned, indicating the owner's data, including the registration date and expiration date.

**Returned Status Codes**:
* 200 OK: success.

## `orders/all/{domain}`

### GET

**Description**: returns a JSON representation of the list of Internet domains purchased by the user who logged in to the portal.

**Parameters**: a path parameter 'email' that identifies the user.

**Request Body**: empty.

**Response**: on success, the JSON representation of the list of purchased Internet domains is returned.

**Returned Status Codes**:
* 200 OK: success.
