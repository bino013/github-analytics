# github-analytics

## Prerequisites
1. Java 14

### How to run
Option 1. Clone the project to your machine then run the command below:
    
    [github-analytics] mvn clean spring-boot:run

Option 2. Package the project the run as java app.
    
    [github-analytics] mvn clean package
    [github-analytics] java -jar target/github-analytics-0.0.1-SNAPSHOT.jar

* Open a browser then go to http://localhost:8080/index

## APIs

* /analytics/search - to search public repository
    * Request Parameter 
        * query - keyword to search
        * size - no of result (max 100)
        
 * /analytics/{owner}/{repo}/commit - to get commit data 
     * Request Parameter 
         * size - no of result (max 100)