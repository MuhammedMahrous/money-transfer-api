# Money Transfer API

This is a toy project for a simple Money Transfer API, 
it exposes rest endpoint to make money transfer between already existing
accounts. It supports making the transaction in different currencies `[USD and EUR]` other
than the source account currency, it uses H2 in-mem database with FlyWay for 
startup data migration from sql script found in : `src/main/resources/db/migration`

To run the application you need JRE11 and Maven
## Install

    mvn clean install

## Run the app

    java -jar target/money-transfer-api-1.0.0-runner.jar 

## Run the tests
	mvn test

# REST API

A **Postman** collection found [here](https://github.com/MuhammedMahrous/money-transfer-api/blob/master/MoneyTransferAPI.postman_collection.json "here") has the basic scenarios for running the app,
except the **No Enough Balance** case which will require making one account of the two
found in the migration script go bankrupt first :)
