# Receipt Processor Submission

### A receipt processor app server is implemented that performs only 2 tasks

1. Processing a given receipt to calculate a score and assigning an ID to the receipt.
2. Providing a score for a given receipt ID.

### Technologies Used
1. Primary Language: Java 
2. Framework: SpringBoot
3. Testing: JUnit for unit testing
4. Containerization: Docker for easy setup and deployment

### How to run

Docker Desktop is the only necessary pre-requisite.

The steps to follow are:

1. Start the Docker Desktop
2. Execute the following commands one by one.
3. ```git clone https://github.com/PriyalShrimali1301/receipt-processor.git```
4. ```cd receipt-processor```
5. ```docker build -t receipt-processor .```
6. ```docker run -p 8080:8080 receipt-processor```

The application creates a container, installs necessary libraries, and conducts initial testing using JUnit.

### Access the application at

```http://localhost:8080/```

The home page will display "Welcome to the Fetch Coding Assignmentt".

### Key Assumptions

I have made several assumptions based on the schema provided in the api.yml file.

To validate retailer names, I initially used the regex pattern ^[\w\s\-]+$, which matches only letters, numbers, spaces, and hyphens. However, the provided example, "M&M Corner Market," includes an ampersand (&), which the original pattern did not permit.

To accommodate this case and ensure consistency with the testing scripts, I revised the regex pattern to ^[\w\s\-&]+$. This updated pattern now allows letters, numbers, spaces, hyphens, and ampersands while still excluding other special characters.

### Checks Applied

The following are the rules we have applied to validate the JSON response:

1. Retailer name:

- Must be a string.
- Cannot be empty.
- Can only contain letters, numbers, spaces, hyphens, and ampersands. This is done based on the pattern ^[\\w\\s\\-&]+$ given in api.yml file along with the assumption mentioned in the previous section.
- It is a required field

2. purchaseDate:

- Must be a string.
- Cannot be empty.
- Must be in format YYYY-MM-DD with valid values. Values like 2020-00-00, 2024-02-31 etc are not allowed.
- It is a required field.
- Cannot be a future date.

3. purchaseTime:

- Must be a string.
- Cannot be empty.
- Must be in format HH:MM (24 Hour Clock) with valid values. Values like 24:00, 99:99 etc are not allowed.
- It is a required field.

4. Items:

- Must be an array containing shortDescription and price.
- Cannot be empty.
- Must have a minimum of 1 field.
- It is a required field.

5. shortDescription:

- Must be a string.
- Cannot be empty.
- Can only contain letters, numbers, spaces, and hyphens. This is done based on the pattern ^[\\w\\s\\-]+$ given in api.yml file.
- It is a required field.

6. price:

- Must be a string ( following the api.yml).
- Cannot be empty or NaN.
- Must be decimal number with 2 decimal places. This restriction is based on the pattern ^\\d+\\.\\d{2}$ given in api.yml file.
- It is a required field.
- Cannot be negative.
- Cannot be zero.


7. total:

- Must be a string ( following the api.yml).
- Cannot be empty or NaN.
- Must be decimal number with 2 decimal places. This restriction is based on the pattern ^\\d+\\.\\d{2}$ given in api.yml file.
- It is a required field.
- Cannot be negative.
- Cannot be zero.


#### Please ensure that the field names in the input JSON maintain consistent spelling.

### Application Details
#### 1. Endpoints
The server exposes 2 endpoints

###### Endpoint 1: Processing receipt
Path: /receipts/process
Method: ```POST```
Payload: Receipt JSON
Response: JSON containing an id for the receipt.
Takes in a JSON body containing the reciept details that follows the following schema mentioned in api.yml and a sample valid receipt is as shown below.
```
{
   "retailer": "Target",
    "purchaseDate": "2022-01-02",
    "purchaseTime": "13:13",
    "total": "1.25",
    "items": [
        {"shortDescription": "Pepsi - 12-oz", "price": "1.25"}
    ]
}
```

The endpoint takes in this receipt data and calculates a score based on the rules provided in the instructions. A random uuid is generated for each reciept and is stored along with the calculated score in the memory.

In response, the API returns a JSON containing the receipt ID as shown below

```{ "id": "7fb1377b-b223-49d9-a31a-5a02701dd310" }```

In case of an invalid receipt, we get a specific response telling us which validation check failed

```
{"error": "Retailer name cannot be empty"}
```

Note that As per instructions, I have not used any database for storage. In memory H2 storage has been used and the application does not persist data i.e all data is lost upon terminating the server. 

Since, the storage is in memory, I have chosen not to store the entire receipt. Only the ID and the score are stored as key-value pairs in the H2 database.  This design is opted because the instructions does not require me to allow updating existing receipts and hence storage of the entire receipt is not necessary.

#### Endpoint 2: Get Receipt points
Path: /receipts/{id}/points
Method: GET
Response: A JSON object containing the number of points awarded.
A simple ```@GetMApping``` endpoint that looks up the receipt by the ID and returns an object specifying the points awarded.

If a receipt with the provided ID is present, the following JSON response is returned:
```
{ "points": 32 }
```

In case of an invalid receipt ID, we get the following response

```
{"error": "No receipt with the given id was found"}
```

### 2. Input validation

Receipt detail validations are handled in the Service class. For each check, a specific error message is returned to ensure that the input JSON contains all the necessary and correctly spelled fields in the proper format.

1. Invalid Purchase Date

```
{
  "retailer": "M&M Corner Market",
  "purchaseDate": "2022-45-20",
  "purchaseTime": "12:33",
  "items": [
    {
      "shortDescription": "Gatorade",
      "price": "8.50"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    }
  ],
  "total": "9.20"
}

```

Response: 

```
{
    "error": "Purchase date format should be YYYY-MM-DD."
}
```

2. Missing Short Description

```
{
  "retailer": "M&M Corner Market",
  "purchaseDate": "2022-15-20",
  "purchaseTime": "12:33",
  "items": [
    {
      
      "price": "8.50"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    }
  ],
  "total": "9.20"
}
```
Response:

```
{
    "error": "Each item must have a non-empty shortDescription of type String."
}
```

3. Invalid Retailer Name

```
{
  "retailer": "M&M Corner {Market",
  "purchaseDate": "2022-11-20",
  "purchaseTime": "12:33",
  "items": [
    {
      "shortDescription": "Gatorade",
      "price": "8.50"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    }
  ],
  "total": "9.20"
}
```
Response:

```
{
    "error": "Retailer name can only contain letters, numbers, spaces, hyphens, and ampersands"
}
```
For each valid data type field, pattern checks are performed using regular expressions. The allowed patterns are defined in the api.yml components section, and the application strictly adheres to these patterns.

Additional checks, as described in the Assumptions section above, have also been implemented.

### 4. Logging

Since this is a small application, light logging is implemented using the ```Log4j2``` library to keep the logging minimal and efficient. This approach ensures that performance remains optimal while still capturing essential log information.

### 3. Testing
Testing has been performed using JUnit. I have designed the tests to validate the points being calculated based on the rules provided.
Score Validation: Tests are written to make sure that the given rules have been implemented properly and correct scores are assigned to receipts.

### 4. Improving the current version

1. Currently, we process one JSON at a time, but there is a scope to enhance performance by implementing batch processing.
2. Although we manage basic errors at present, there is potential to incorporate more comprehensive checks to cover a wider array of exceptions.
3. Logging can be improved to manage different functionalities.
4. Enhance the test suite by adding more unit tests. Currently, the code only validates the points calculated for a receipt. We can expand it to include unit tests for various endpoints and exceptions.


### 5. Application Overview

Considering that this was a task focussing on the backend skills, I have not added any user interface. A basic HTML file is created to show the landing page.

Postman was used to test the different endpoints and their functionalities.
