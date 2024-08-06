package com.fetch.receipt_processor.service;

import com.fasterxml.jackson.databind.JsonNode;

import com.fetch.receipt_processor.controller.ReceiptController;
import com.fetch.receipt_processor.model.Receipt;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fetch.receipt_processor.repository.ReceiptRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service

public class ReceiptService {

    private static final Logger logger = LogManager.getLogger(ReceiptController.class);

    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public Map<String, String> saveReceipt(JsonNode json) {
        logger.info("ReceiptService saveReceipt: Entered");
        Map<String, String> response = new HashMap<>();
        String validationError = validateReceipt(json);

        if (!validationError.isEmpty()) {
            response.put("error", validationError);
            logger.info("ReceiptService validationError: "+validationError);
            return response;
        }

        int totalPoints = calculatePoints(json);
        Receipt receipt = new Receipt(totalPoints);
        receiptRepository.save(receipt);
        response.put("id", receipt.getId());

        return response;
    }

    public Optional<Receipt> getReceiptById(String id) {
        return receiptRepository.findById(id);
    }

    private String validateReceipt(JsonNode json) {

        //Retailer Validation Checks
        if (!json.has("retailer") || json.get("retailer").asText().isEmpty()) {
            //throw new ValidationException("Retailer name cannot be empty");
            return("Retailer name cannot be empty");
        }

        String retailerName = json.get("retailer").asText();
        String pattern = "^[\\w\\s\\-&]+$";
        if (!retailerName.matches(pattern)) {
            //throw new ValidationException("Retailer name can only contain letters, numbers, spaces, hyphens, and ampersands");
            return("Retailer name can only contain letters, numbers, spaces, hyphens, and ampersands");
        }

        //Check on total amount
        if (!json.has("total") || !json.get("total").isTextual()) {
            //throw new ValidationException("Total amount is required and must be a String");
            return("Total amount is required and must be a String");
        }

        if (!json.get("total").asText().matches("^\\d+\\.\\d{2}$")) {
            //throw new ValidationException("Total amount must be a decimal number with exactly 2 decimal places.");
            return("Total amount must be a decimal number with exactly 2 decimal places.");
        }


        if(Double.isNaN(Double.parseDouble(json.get("total").asText())) ){
            //throw new ValidationException("Total amount should be a number.");
            return("Total amount should be a number");
        }


        if(!(Double.parseDouble(json.get("total").asText()) > 0.0)){
            //throw new ValidationException("The amount must be greater than 0.");
            return("The amount must be greater than 0.");
        }


        //Items Validations
        if (!json.has("items") || !json.get("items").isArray()) {
            //throw new ValidationException("Items are required and must be an array");
            return("Items are required and must be an array");
        }

        JsonNode items = json.get("items");
        for (JsonNode item : items) {
            if (!item.has("shortDescription") || !item.get("shortDescription").isTextual() || item.get("shortDescription").asText().isEmpty()) {
                //throw new ValidationException("Each item must have a non-empty shortDescription of type String.");
                return("Each item must have a non-empty shortDescription of type String.");
            }
            if(!item.get("shortDescription").asText().matches(pattern)){
                //throw new ValidationException("Short Description contains invalid characters.");
                return("Short Description contains invalid characters.");
            }
            if (!item.has("price") || !item.get("price").isTextual()) {
                //throw new ValidationException("Price is required and must be a String");
                return("Price is required and must be a String");
            }


            if (!item.get("price").asText().matches("^\\d+\\.\\d{2}$")) {
                //throw new ValidationException("Price must be a decimal number with exactly 2 decimal places.");
                return("Price must be a decimal number with exactly 2 decimal places.");
            }

            if (Double.isNaN(item.get("price").asDouble())) {
                //throw new ValidationException("Price should be a number.");
                return("Price should be a number.");
            }

            if(!(Double.parseDouble(item.get("price").asText()) > 0)){
                //throw new ValidationException("Price must be greater than 0.");
                return("Price must be greater than 0.");
            }
        }

        //Purchase Date Validation Checks
        if (!json.has("purchaseDate") || json.get("purchaseDate").asText().isEmpty() || !json.get("purchaseDate").isTextual()) {
            //throw new ValidationException("Purchase date is required and should be a String.");
            return("Purchase date is required and should be a String.");
        }

        if(!json.get("purchaseDate").asText().matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")){
            //throw new ValidationException("Purchase date format should be YYYY-MM-DD.");
            return("Purchase date format should be YYYY-MM-DD.");
        }

        if(!isValidDate(json.get("purchaseDate").asText())){
            //throw new ValidationException("Purchase date cannot be a date from the future");
            return("Purchase date cannot be a date from the future");
        }

        //Purchase Time Checks
        if (!json.has("purchaseTime") || json.get("purchaseTime").asText().isEmpty() || !json.get("purchaseTime").isTextual() ||
                !json.get("purchaseTime").asText().matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$")) {
            //throw new ValidationException("Purchase time is invalid. It must be in format HH:MM (24-hour clock) with valid values.");
            return("Purchase time is invalid. It must be in format HH:MM (24-hour clock) with valid values.");
        }

        return "";
    }

    private static boolean isValidDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public int calculatePointsForTesting(JsonNode jsonNode) {
        logger.info("ReceiptService calculatePointsForTesting:");
        return calculatePoints(jsonNode);
    }

    private int calculatePoints(JsonNode json) {
        int totalPoints = 0;
        String retailerName = json.get("retailer").asText();
        logger.info("ReceiptService calculatePointsForTesting: "+totalPoints+ " at beginning");
        // One point for every alphanumeric character in the retailer name.
        for (char c : retailerName.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                totalPoints += 1;
            }
        }

        // 50 points if the total is a round dollar amount with no cents.
        double total = json.get("total").asDouble();
        int totalInt = (int) total;

        if (totalInt == total) {
            totalPoints += 50;
        }

        // 25 points if the total is a multiple of 0.25
        if ((total * 100) % (0.25 * 100) == 0) {
            totalPoints += 25;
        }

        // 5 points for every two items on the receipt.
        JsonNode itemsNode = json.get("items");
        int itemPoints = itemsNode.size() / 2;
        totalPoints += 5 * itemPoints;

        // If the trimmed length of the item description is a multiple of 3,
        // multiply the price by 0.2 and round up to the nearest integer. The result is the number of points earned.
        for (JsonNode itemDes : itemsNode) {
            String description = itemDes.get("shortDescription").asText();
            if (description.trim().length() % 3 == 0) {
                int price = itemDes.get("price").asInt();
                int points = (int) Math.ceil(price * 0.2);
                totalPoints += points;
            }
        }

        // 6 points if the day in the purchase date is odd.
        String dateString = json.get("purchaseDate").asText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate purchaseDate = LocalDate.parse(dateString, formatter);

        if (purchaseDate.getDayOfMonth() % 2 == 1) {
            totalPoints += 6;
        }

        // 10 points if the time of purchase is after 2:00pm and before 4:00pm.
        String timeString = json.get("purchaseTime").asText();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime purchaseTime = LocalTime.parse(timeString, timeFormatter);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        if (!purchaseTime.isBefore(startTime) && !purchaseTime.isAfter(endTime)) {
            totalPoints += 10;
        }
        logger.info("ReceiptService calculatePointsForTesting: "+totalPoints+ " at end");
        return totalPoints;
    }
}