###### Part 1

go to https://mockaroo.com/ and generate id and gender excel file, download and place in resources folder

go to https://mockaroo.com/ and generate id and gender sql table, create a database and put this table in that database

create a test where you compare genders from mysql and excel by id

expect around 50% of test cases to fail, it's ok
###### Part 2

go to https://mockaroo.com/ and generate id, name, gender, fee, and number field called multiplier sql table called students,
 create a database and put this table in that database
 
the multiplier field must be from 0.5 to 2.0 with 2 decimal places

create a test case where update the student's fee using multiplier and write it to database, after updating to read from database again and assert that it's correctly updated
 
expect around 20-30% of test cases to fail

###### Requirements
tests should run with testng.xml

databse url, username and password must be provided as a parameter from testng.xml

 
###### Part 3 (Optional)
In https://github.com/technoStudy/BasqarTestSuite in Group 1 testcase, read student firstname, lastname and gender from database by id, create a dataprovider that creates 3 student ids to fetch from database(should not be random ids)
