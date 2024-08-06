package com.fetch.receipt_processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.receipt_processor.service.ReceiptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReceiptProcessorApplicationTests {


	@Autowired
	private ReceiptService receiptService;
	private final ObjectMapper objectMapper = new ObjectMapper();


	@Test
	void contextLoads() {
	}

	@Test
	public void testCalculateReceiptScore1() {

		String jsonString = "{\n" +
				"  \"retailer\": \"Target\",\n" +
				"  \"purchaseDate\": \"2022-01-01\",\n" +
				"  \"purchaseTime\": \"13:01\",\n" +
				"  \"items\": [\n" +
				"    {\n" +
				"      \"shortDescription\": \"Mountain Dew 12PK\",\n" +
				"      \"price\": \"6.49\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Emils Cheese Pizza\",\n" +
				"      \"price\": \"12.25\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Knorr Creamy Chicken\",\n" +
				"      \"price\": \"1.26\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Doritos Nacho Cheese\",\n" +
				"      \"price\": \"3.35\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"   Klarbrunn 12-PK 12 FL OZ  \",\n" +
				"      \"price\": \"12.00\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"total\": \"35.35\"\n" +
				"}";

		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			int points = receiptService.calculatePointsForTesting(jsonNode);
			assertEquals(28, points);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCalculateReceiptScore2() {
		String jsonString = "{\n" +
				"  \"retailer\": \"M&M Corner Market\",\n" +
				"  \"purchaseDate\": \"2022-03-20\",\n" +
				"  \"purchaseTime\": \"14:33\",\n" +
				"  \"items\": [\n" +
				"    {\n" +
				"      \"shortDescription\": \"Gatorade\",\n" +
				"      \"price\": \"2.25\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Gatorade\",\n" +
				"      \"price\": \"2.25\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Gatorade\",\n" +
				"      \"price\": \"2.25\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Gatorade\",\n" +
				"      \"price\": \"2.25\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"total\": \"9.00\"\n" +
				"}";

		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			int points = receiptService.calculatePointsForTesting(jsonNode);
			assertEquals(109, points);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCalculateReceiptScore3() {
		String jsonString = "{\n" +
				"    \"retailer\": \"Walgreens\",\n" +
				"    \"purchaseDate\": \"2022-01-02\",\n" +
				"    \"purchaseTime\": \"08:13\",\n" +
				"    \"total\": \"2.65\",\n" +
				"    \"items\": [\n" +
				"        {\"shortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"},\n" +
				"        {\"shortDescription\": \"Dasani\", \"price\": \"1.40\"}\n" +
				"    ]\n" +
				"}";

		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			int points = receiptService.calculatePointsForTesting(jsonNode);
			assertEquals(15, points);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCalculateReceiptScore4() {
		String jsonString = "{\n" +
				"    \"retailer\": \"Target\",\n" +
				"    \"purchaseDate\": \"2022-01-02\",\n" +
				"    \"purchaseTime\": \"13:13\",\n" +
				"    \"total\": \"1.25\",\n" +
				"    \"items\": [\n" +
				"        {\"shortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}\n" +
				"    ]\n" +
				"}";

		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			int points = receiptService.calculatePointsForTesting(jsonNode);
			assertEquals(31, points);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




}
