# Distributed Systems Project 2023-2024

This platform allows users to quickly check the availability of desired domains, proceed with secure purchases, and easily manage registered domains.

## Main Features

1. **Registration and Login:** Users can create a new account or log in to an existing account to start using the platform's services.
2. **Domain Search:** By entering the desired domain name, users can check real-time availability and get information about any existing owners.
3. **Domain Purchase:** Once a domain is found to be available, users can buy it by securely entering payment details.
4. **Domain Management:** Users can view a list of domains they own, monitor expiration dates, and renew domains as needed.
5. **Order History:** The platform offers a dedicated section where users can view all past orders, with details on each transaction.

## Group Members

* Youssef Bimezzagh (894506) <y.bimezzagh@campus.unimib.it>
* Leonardo Bizzoni (899629) <l.bizzoni@campus.unimib.it>
* Gianluca Rota (904375) <g.rota35@campus.unimib.it>

## Compilation and Execution

Both the Web server and the database are Java applications managed with Maven. Inside the respective folders, you can find the `pom.xml` file which contains the Maven configuration for the project. It is assumed that the laboratory virtual machine is being used, so Java 21 is specified in the `pom.xml`.

The Web server and the database are Java projects that use Maven to manage dependencies, compilation, and execution.

### Web Client

To launch the Web client, use the "Live Preview" extension on Visual Studio Code, as demonstrated during the lab sessions. This extension exposes a local server with the files contained in the `client-web` folder.

**Note:** It is necessary to configure CORS in Google Chrome as shown in the lab.

Main success scenario and variations:

A user visits the website to register a domain for their new blog. They access the homepage and see the options for login and registration. Since they are a new user, they click on "Register," fill out the registration form with their name, surname, and email address, and submit the form. They receive a confirmation that the registration was successful and are redirected.

The user enters "mynewblog.com" into the domain check field and clicks "Check Availability." The system checks and confirms that the domain is available. The domain purchase section is displayed, allowing the user to proceed. They enter their payment details and confirm. The payment is successfully processed, and the user receives a purchase confirmation.

In another situation, the user might try to register "popularsite.com," a domain already owned by someone else. They enter the domain name, and the system checks that it is not available. The site displays a message "Domain not available!" along with details of the current owner.

Additionally, the user might try to register "myoldblog.com," a domain they previously registered. They enter the domain name, and the system recognizes that the domain is already owned by them, displaying the message "Owned by you!" without allowing them to proceed with the purchase.

If the user wants to renew one of their existing domains, they click on "View Domains" under the "Your Information" section. A list of their domains is displayed with renewal options. The user selects "renew" for a domain nearing expiration, chooses the renewal period (e.g., 2 years), and completes the payment through the payment pop-up. During this phase, the system restricts the renewal period so that the total registration duration does not exceed 10 years. For example, if a domain expires in 8 years, the user can only select up to 2 years of renewal.

In another scenario, the user can check their order history by clicking on "View Orders." They see all details of past orders, including purchased domains, order dates, order types (registrations or renewals), and amounts paid.

### Web Server

The Web server uses Jetty and Jersey. It can be started by running `mvn jetty:run` within the `server-web` folder. It exposes the REST APIs at `localhost` on port `8080`.

### Database

The database is a simple Java application. The following Maven commands can be used:

* `mvn clean`: to clean up temporary files,
* `mvn compile`: to compile the application,
* `mvn exec:java`: to start the application (assumes the main class is `Main.java`). It listens on `localhost` at port `3030`.
