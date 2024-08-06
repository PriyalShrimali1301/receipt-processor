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

import java.util.HashMap;
import java.util.Map;

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

	@Test
	public void testCalculateReceiptScore6() {

		String jsonString = "{\n" +
				"  \"retailer\": \"Target\",\n" +
				"  \"purchaseDate\": \"2022-01-01\",\n" +
				"  \"purchaseTime\": \"13:01\",\n" +
				"  \"items\": [\n" +
				"    {\n" +
				"      \"shortDescription\": \"Mountain {Dew 12PK\",\n" +
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
			Map<String, String> expected = new HashMap<>();
			expected.put("error", "Short Description contains invalid characters.");

			Map<String, String> actual= receiptService.saveReceipt(jsonNode);
			assertEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCalculateReceiptScore7() {

		String jsonString = "{\n" +
				"  \"retailer\": \"Target\",\n" +
				"  \"purchaseDate\": \"2022-13-01\",\n" +
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
			Map<String, String> expected = new HashMap<>();
			expected.put("error", "Purchase date format should be YYYY-MM-DD.");

			Map<String, String> actual= receiptService.saveReceipt(jsonNode);
			assertEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCalculateReceiptScore8() {

		String jsonString = "{\n" +
				"  \"retailer\": \"Target\",\n" +
				"  \"purchaseDate\": \"2022-11-01\",\n" +
				"  \"purchaseTime\": \"13:01\",\n" +
				"  \"items\": [\n" +
				"    {\n" +
				"      \"shortDescription\": \"Mountain Dew 12PK\",\n" +
				"      \"price\": \"6.49\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Emils Cheese Pizza\",\n" +
				"      \"price\": \"12.5\"\n" +
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
			Map<String, String> expected = new HashMap<>();
			expected.put("error", "Price must be a decimal number with exactly 2 decimal places.");

			Map<String, String> actual= receiptService.saveReceipt(jsonNode);
			assertEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCalculateReceiptScore9() {

		String jsonString = "{\n" +
				"  \"retailer\": \"Target\",\n" +
				"  \"purchaseDate\": \"2022-11-01\",\n" +
				"  \"purchaseTime\": \"13:01\",\n" +
				"  \"items\": [\n" +
				"    {\n" +
				"      \"shortDescription\": \"Mountain Dew 12PK\",\n" +
				"      \"price\": \"6.49\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Emils Cheese Pizza\",\n" +
				"      \"price\": \"12.50\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Knorr Creamy Chicken\",\n" +
				"      \"price\": \"1.26\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Doritos Nacho Cheese\",\n" +
				"      \"price\": \"3.35\"\n" +
				"    },{\n" +

				"      \"price\": \"12.00\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"total\": \"35.35\"\n" +
				"}";

		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			Map<String, String> expected = new HashMap<>();
			expected.put("error", "Each item must have a non-empty shortDescription of type String.");

			Map<String, String> actual= receiptService.saveReceipt(jsonNode);
			assertEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public void testCalculateReceiptScore10() {

		String jsonString = "{\n" +
				"  \"retailer\": \"Target\",\n" +
				"  \"purchaseDate\": \"2022-11-01\",\n" +
				"  \"purchaseTime\": \"125:01\",\n" +
				"  \"items\": [\n" +
				"    {\n" +
				"      \"shortDescription\": \"Mountain Dew 12PK\",\n" +
				"      \"price\": \"6.49\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Emils Cheese Pizza\",\n" +
				"      \"price\": \"12.50\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Knorr Creamy Chicken\",\n" +
				"      \"price\": \"1.26\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Doritos Nacho Cheese\",\n" +
				"      \"price\": \"3.35\"\n" +
				"    },{\n" +
				"      \"shortDescription\": \"Doritos Sour Cream Cheese\",\n" +
				"      \"price\": \"12.00\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"total\": \"35.35\"\n" +
				"}";

		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			Map<String, String> expected = new HashMap<>();
			expected.put("error", "Purchase time is invalid. It must be in format HH:MM (24-hour clock) with valid values.");

			Map<String, String> actual= receiptService.saveReceipt(jsonNode);
			assertEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
