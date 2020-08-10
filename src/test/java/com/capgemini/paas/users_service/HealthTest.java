package com.capgemini.paas.users_service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UserApplication.class)
@AutoConfigureMockMvc
public class HealthTest {

	@Autowired
    private TestRestTemplate testRestTemplate;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Test
    public void testCheckHealth_OK() {
    	
    	ResponseEntity<String> userResponse = testRestTemplate.getForEntity("/operational/health", String.class);
    	
    	JsonNode root;
		try {
			
			root = mapper.readTree(userResponse.getBody());
			
			assertEquals(HttpStatus.OK, userResponse.getStatusCode());
			assertEquals("UP", root.path("status").asText());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}
        
    }
    
    @Test
    public void testCheckInfo_OK() {
    	
    	ResponseEntity<String> userResponse = testRestTemplate.getForEntity("/operational/info", String.class);
    	
    	assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        
    }
    
//    @Test
//    public void testCheckPrometheus_OK() {
//    	
//    	ResponseEntity<String> userResponse = testRestTemplate.getForEntity("/operational/prometheus", String.class);
//    	
//    	assertEquals(HttpStatus.OK, userResponse.getStatusCode());
//        
//    }

}
