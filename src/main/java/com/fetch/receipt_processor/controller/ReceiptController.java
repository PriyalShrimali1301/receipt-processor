package com.fetch.receipt_processor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.fetch.receipt_processor.model.Receipt;
import com.fetch.receipt_processor.service.ReceiptService;
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
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> response = new HashMap<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            response = receiptService.saveReceipt(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @GetMapping("/{id}/points")
    public Map<String, Integer> getPoints(@PathVariable String id) {
        Optional<Receipt> receiptOpt = receiptService.getReceiptById(id);
        Map<String, Integer> response = new HashMap<>();
        if (receiptOpt.isPresent()) {
            Receipt receipt = receiptOpt.get();
            response.put("points", receipt.getPoints());
        } else {
            response.put("points", -1);
        }
        return response;
    }
}
