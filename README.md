# Microservice that invokes AWS elastic search and make it available using API gateway

This is a demo app which returns results based on query parameters

# Problem Statement

Using Java, write a microservice that invokes AWS elastic search and make it available using API gateway.  

1. Test Data - http://askebsa.dol.gov/FOIA%20Files/2017/Latest/F_5500_2017_Latest.zip
2. Search should be allowed by Plan name, Sponsor name and Sponsor State
3. Use AWS best practices 
4. Well documented and tested.

# Steps Involved 

* Created a elastic search domain
* Download the sample files in the esSetup folder
* Run createSetupAndDataFile.java to create json insert files for the mapping and data
* Run setup.sh and data.sh to push data in AWS ElasticSearch
* Create AWS Lamda function and write the Lamda function with the help of eclipse plugin
* Create jar through maven and upload your jar into Lamda console
* Add API-Gateway trigger to your newly created Lamda function
* Add url parameters to API-Gateway to be passed to Lamda function

# Demo Application can be tested via API endpoint. Below are few examples

https://2sgvkxrc6j.execute-api.us-west-1.amazonaws.com/default/esearch

https://2sgvkxrc6j.execute-api.us-west-1.amazonaws.com/default/esearch?PLAN_NAME=MORAVIAN%20MAN

https://2sgvkxrc6j.execute-api.us-west-1.amazonaws.com/default/esearch?PLAN_NAME=MORAVIAN%20MAN&SPONSOR_NAME=PLAYPOW

https://2sgvkxrc6j.execute-api.us-west-1.amazonaws.com/default/esearch?PLAN_NAME=MORAVIAN%20MANORS%20INC%20HEALTH%20CARE%20PLA&SPONSOR_NAME=PLAYPOW&SPONSOR_STATE=MONET
