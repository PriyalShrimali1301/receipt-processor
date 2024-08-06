package com.fetch.receipt_processor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.fetch.receipt_processor.model.Receipt;
import com.fetch.receipt_processor.service.ReceiptService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;
    private static final Logger logger = LogManager.getLogger(ReceiptController.class);

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/process")
    public Map<String, String> addReceipt(@RequestBody String json) {
        logger.info("ReceiptController addReceipt: Entered");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> response = new HashMap<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            response = receiptService.saveReceipt(jsonNode);
            logger.info("ReceiptController: Response Received");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @GetMapping("/{id}/points")
    public Map<String, String> getPoints(@PathVariable String id) {
        logger.info("ReceiptController getPoints: Entered");
        Optional<Receipt> receiptOpt = receiptService.getReceiptById(id);
        Map<String, String> response = new HashMap<>();
        if (receiptOpt.isPresent()) {
            Receipt receipt = receiptOpt.get();
            response.put("points", String.valueOf(receipt.getPoints()));
            logger.info("ReceiptController getPoints: Response Received");
        } else {
            logger.info("No receipt with the given id was found");
            response.put("error", "No receipt with the given id was found");
        }
        return response;
    }
}
