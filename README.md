# receipt-processor

## Receipt Processor Submission
### A receipt processor app server is implemented that performs only 2 tasks

1. Processing a given receipt to calculate a score and assigning an ID to the receipt.
2. Providing a score for a given receipt ID.

### Technologies Used
1. Primary Language: JavaScript (Node.js)
2. Framework: Express
3. Testing: Jest for unit testing
4. Containerization: Docker for easy setup and deployment

### How to run
Docker Desktop is the only necessary pre-requisite.

The steps to follow are:

1. Start Docker Desktop
2. Execute the following commands one by one.
3. git clone https://github.com/PriyalShrimali1301/receipt-processor.git
4. cd receipt-processor
5. docker build -t receipt-processor .
6. docker run -p 8080:8080 receipt-processor
The application creates a container, installs necessary libraries, and conducts initial testing using JUnit.

### Access the application at

http://localhost:8080/
The home page will display "Welcome to the Fetch Coding Assignmentt".

### Key Assumptions
I have made some assumptions based on the schema provided in api.yml file.

For validating retailer names, the regex pattern ^[\w\s\-]+$ is initially used. This pattern matches only letters, numbers, spaces, and hyphens. However, in the provided example, "M&M Corner Market" is considered valid despite containing an ampersand (&), which was not originally allowed by the pattern.

To accommodate this case and ensure consistency with testing scripts, I updated the regex to also permit ampersands. The revised pattern ^[\w\s\-&]+$ now allows letters, numbers, spaces, hyphens, and ampersands, while still excluding other special characters.

### Checks Applied

Some checks the app does for each field are as follows (strictly based on api.yml):

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
- Cannot be more than 10e10. This is a design choice to avoid unrealistic price values.

7. total:

- Must be a string ( following the api.yml).
- Cannot be empty or NaN.
- Must be decimal number with 2 decimal places. This restriction is based on the pattern ^\\d+\\.\\d{2}$ given in api.yml file.
- It is a required field.
- Cannot be negative.
- Cannot be zero.
- Cannot be more than 10e10. This is a design choice to avoid unrealistic total values.

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

The database can be accessed using 
```
localhost:8080/h2-console
```
Since, the storage is in memory, I have chosen not to store the entire receipt. Only the ID and the score are stored as key-value pairs in the H2 database.  This design is opted because the instructions does not require me to allow updating existing receipts and hence storage of the entire receipt is not necessary.

##### Endpoint 2: Get Receipt points
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
{"points": -1}
```

### 2. Input validation

Validations of provided receipt details input are performed in the Service class:

For each valid data type field, pattern checks are conducted using regex. The allowed patterns are described in the api.yml components section and the application follows these patterns strictly. 

Some other checks have been added which have been described in the Assumption section above.

### 4. Logging

Since this is a small application, light logging is implemented using the ```Log4j2``` library to keep the logging minimal and efficient. This approach ensures that performance remains optimal while still capturing essential log information.

### 3. Testing
Testing has been performed using JUnit. I have designed the tests to validate a score.
Score Validation: Tests are written to make sure that the given rules have been implemented properly and correct scores are assigned to receipts.


### 4. Accessing the server

For this particular task, no UI has been created except the basic html scripts to show errors.
Postman was used to test the different endpoints and their functionalities.
