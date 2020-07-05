# budget-management
Project developed for the advanced programming techniques exam of the university of Florence. The project consists of a java application for managing a company's turnover.

<p>
  
  <a>[![Build Status](https://travis-ci.com/AlessandroArezzo/budget-management.svg?branch=master)](https://travis-ci.com/AlessandroArezzo/budget-management)</a>
  <a>[![Coverage Status](https://coveralls.io/repos/github/AlessandroArezzo/budget-management/badge.svg?branch=master)](https://coveralls.io/github/AlessandroArezzo/budget-management?branch=master)</a>
</p>

<p>
  
  <a>[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=alert_status)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
  <a>[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=bugs)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
  <a>[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=code_smells)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
  <a>[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
  <a>[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
</p>

<p>
  
  <a>[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
  <a>[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
  <a>[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=security_rating)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
  <a>[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com%3Abudget-management&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com%3Abudget-management)</a>
</p>

<h2>Prerequisites</h2>
To use the code you need to have an internet connection for the first run and installed:
<ul>
<li>Maven</li>
<li>Docker</li>
</ul>

<h2>To run the tests</h2>
To run the tests you need to type the following command from the project's root directory:<br>
mvn -f budget-management/pom.xml clean verify
<h3>To perform code coverage and mutation testing</h3>
To start the tests by enabling the execution of the jacoco plugin for calculating the code coverage and the pit plugin for mutation testing is necessary to type the previous command with add enabled the respective profiles as follows:<br>
mvn -f budget-management/pom.xml clean verify -Pjacoco,mutation-testing

<h2>To run the application</h2>
To start the application you must
<ol>
<li>Build the package file with the following command from the project root directory:<br>
  mvn -f budget-management/pom.xml -DskipTests=true package </li>
<li>Start the docker container of the MongoDB server with the command:<br>
  mvn -f budget-management/pom.xml docker:start</li>
  <li>Start the application with the command:<br> 
    java -jar budget-management/target/budget-management-0.0.1-SNAPSHOT-jar-with-dependencies.jar </li>
</ol>
When starting the application, you can also specify the following parameters:<br>
<ul>
  <li>--mongo-host: host name of the MongoDB server</li>  
  <li>--mongo-port: number of port of the MongoDB server</li> 
  <li>--db-name: name of the database to use</li> 
  <li>--collection-clients-name: name of the collections of clients in database</li>
  <li>--collection-invoices-name: name of the collections of invoices in database</li> 
</ul>
To specify the value of one of these parameters, add the syntax name_of_parameter value_of_parameter to the start command of the jar file.
Their default value will be used for unspecified parameters.
