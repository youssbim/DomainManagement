# Distributed Systems Project 2023-2024 - TCP

Documentation of the TCP protocol for the project "Internet Domain Purchase and Release Management".
The implemented protocol is a text-based protocol "*inspired*" by HTTP:
- read access requests to a resource are prefixed by `GET`
- write access requests to a resource are prefixed by `POST`

For each request made:
- if the number of parameters is less than the expected number of parameters, `GARBAGE_REQUEST InvalidNumberOfParameters` will be returned, indicating that the request is invalid due to missing parameters.

## Database Structure
The document-based database manages 4 *entities* represented as JSON objects:
- *User*
- *Registration*
- *Order*
- *Domain*

To optimize performance, the database uses a *Least Recently Used Cache* that can contain up to 5000 entities per type. When this limit is exceeded, the entity with the fewest accesses is removed from the cache and saved to disk in a JSON-formatted text file. If a user later requests a removed entity, the corresponding file is retrieved, and the entity instance is reconstructed using the `JSONParser` class, then reinserted into the cache.

The JSON files saved on disk are located in the `database/db-content` directory and are divided by entity type:
- `database/db-content/users`: information related to registered users.
- `database/db-content/registrations`: information related to domain registrations.
- `database/db-content/orders`: information related to orders placed by users.
- `database/db-content/domains`: information related to active and inactive domains.

## Entity Structure
### User
The `User` entity consists of the following parameters:
- `name`.
- `surname`.
- `email`.
- a list of [registrations](#registration) `registered_domains`.
- a list of [orders](#order) `orders`.

### Registration
The `Registration` entity consists of the following parameters:
- `id`.
- the email of the user who registered the domain `done_by`.
- the instance of the [domain](#domain) to which the registration refers `referring_to`.
- the registration date `registered_on`.
- the domain expiration date `expires_on`.

### Order
The `Order` entity consists of the following parameters:
- `id`.
- the date the order was placed `ordered_on`.
- the order type `type` chosen from:
  + registration: encoded with the value `0`.
  + renewal: encoded with the value `1`.
- the payment amount `payment_amount`.
- the instance of the [domain](#domain) to which the order refers `referring_to`.
- the email of the user who placed the order `done_by`.

### Domain
The `Domain` entity consists of the following parameters:
- the domain name `name`.
- the email of the user who has currently registered the domain `done_by`.
- the identifier of the [registration](#registration) of the domain `registration_id`.

## `GET /domains [domain_name]`

**Description**: given the name of a domain, it searches the database to see if it exists and returns a JSON-formatted string containing information about the domain.

**Parameters**: a string containing the domain name to search for.

**Response**:
- **Domain not found**: the requested domain is not present in the database and is therefore available for registration, the returned JSON will be: `{ "available": true }`
- **Domain found**: the requested [domain](#domain) is present in the database, the returned JSON will be: `{ "available": false, "owner": stringified_owner_object, "registered_on": "registration_date", "expires_on": "expiration_date" }`

## `GET /domains/all [user_email]`

**Description**: given the email address of a registered user, returns a JSON-formatted string containing a list of domains that have been purchased by the user. These domains may have expired or may belong to another user if the original owner did not renew them in time.

**Parameters**: a string containing the email address of a registered user.

**Response**:
- **Email not found**: the entered email does not belong to any registered user, `GARBAGE_REQUEST UserDoesNotExists` will be returned.
- **User found**: the entered email belongs to a registered user, the returned JSON will be: `{ "response": [{ "name": "domain_name", "owner": "owner_email", "registration": stringified_registration_object }, ... ]}`

## `GET /orders/done_by [user_email]`

**Description**: given the email address of a registered user, returns a JSON-formatted string containing the list of [orders](#orders) placed by the user identified by the email address.

**Parameters**: a string containing the email address of a registered user.

**Response**:
- **Email not found**: the entered email does not belong to any registered user, `GARBAGE_REQUEST UserDoesNotExists` will be returned.
- **User found**: the entered email belongs to a registered user, the returned JSON will be: `{ "response": [ stringified_order_object, ... ]}`

## `GET /userinfo [user_email]`

**Description**: given the email address of a registered user, returns a JSON-formatted string containing all information about the [user](#user) identified by the email address.

**Parameters**: a string containing the email address of a registered user.

**Response**:
- **Email not found**: the entered email does not belong to any registered user, `GARBAGE_REQUEST UserDoesNotExists` will be returned.
- **User found**: the entered email belongs to a registered user, the returned JSON will be the stringified instance of the [user](#user) JSON object.

## `POST /user/register [user_name] [user_surname] [user_email]`

**Description**: Given a name, surname, and email address, creates an instance of [user](#user) in the database, initially saving it only in cache.

**Parameters**: 3 strings containing the name, surname, and email address of a **non-registered** user.

**Response**:
- **Email already in DB**: the entered email belongs to an existing user in the database, `GARBAGE_REQUEST UserAlreadyExists` will be returned.
- **Email not found**: the entered email does not belong to any user, so a user instance is created and saved in cache. `OK` will be returned.

## `POST /orders [user_email] [domain_name] [order_type] [payment_amount] [years_of_registration]`

**Description**:
- the email of the user who wants to register the domain `user_email`.
- the name of the domain to register `domain_name`.
- a string representing the order type:
  + to register a new domain or a domain that has expired `register`.
  + to renew a previously registered and still active domain `renewal`.
- the payment amount `payment_amount`.
- the number of years for which the domain should be registered/renewed `years_of_registration`. This parameter cannot exceed 10 years.

**Parameters**: 3 strings containing the name, surname, and email address of a **non-registered** user.

**Response**:
- **Email not found**: the entered email does not belong to any user, so the order cannot be placed. `GARBAGE_REQUEST UserDoesNotExists` will be returned.
- **Registration of an already registered and still active domain**: the entered domain name identifies a domain already registered by a user and is still active. `GARBAGE_REQUEST DomainAlreadyRegistered` will be returned.
- **Order already exists**: if the order is found in the DB, `GARBAGE_REQUEST OrderAlreadyExists` will be returned.
- **Order duration exceeds 10 years**: if the registration/renewal duration exceeds 10 years, `GARBAGE_REQUEST RegisteredForMoreThanTenYears` will be returned.
- **Renewal of an unregistered domain**: if the renewal request is for a domain not present in the DB, `GARBAGE_REQUEST DomainNotRegistered` will be returned.
- **Renewal of a domain owned by a different user**: if the renewal request is for a domain present in the DB but owned by a different user, `GARBAGE_REQUEST DomainOwnedByDifferentUser` will be returned.
- **Renewal of an expired domain**: if the renewal request is for a domain present in the DB but whose expiration date has passed, `GARBAGE_REQUEST RenewalRequestedForExpiredDomain` will be returned.
- **Renewal of a domain for more than 10 years**: if the renewal request is for a domain in the DB whose total registration years exceed 10, `GARBAGE_REQUEST RenewedForMoreThanTenYears` will be returned.
- **Valid order**: if the order is valid and does not fall into the previous scenarios, `OK` will be returned.

## Any Other Format
For any other request format, `GARBAGE_REQUEST` will simply be returned, and the request will be completely ignored.
