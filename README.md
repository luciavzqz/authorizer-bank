<h1 align="center">Code Challenge: Authorizer</h1>

Description and context
---
---
I was asked to implement an application that authorizes transactions for a specific account following a set of predefined rules.

Development tools
---
---
For the development of this project I used:
- JDK 8
- Maven

Design decisions
---
---
- **Java as language**: I chose Java because it is the language I feel most comfortable with and have the most experience with. On the positive side, it is stable and has great performance along with extensive documentation.
- **Maven as the build automation tool**.
- **Junit for tests**: I chose Junit to run tests because I have previous experience with it.
- **Design pattens**: I chose the chain of responsibility pattern for the application of business rules, as it allows for easy insertion of future new business rules, complying with the SOLID principles of single responsibility and open/closed, and also allows for easy future modification of the chain of responsibility.
- **JSON parser**: I chose Jackson as a JSON parser due to it has the best performance when working with large files.
- **Plugins**:
  - I used Lombok as a plugin to achieve a cleaner code.
  - I used the maven-assembly plugin in order to build the application with the Jackson dependency.

How to run the project
---
---
1. *Set up the project.* Download the sources and documentation of the project with Maven.
2. *Generates the java .jar application.* Position yourself in the root of the project with the console and run:


    mvn clean install

3. *Run the application.* From the root of the project you can enter the following command, where "operations" is the full path to the operations file.
   
    
    java -jar ./target/authorizer-1.0-jar-with-dependencies.jar operations

4. *Get the result.* The file with the application response will remain in the root of the project. You can see it running:

    
    cat authorized-operations