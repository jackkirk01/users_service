package com.capgemini.paas.users_service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.capgemini.paas.users_service.model.User;
import com.capgemini.paas.users_service.persistence.dao.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UserApplication.class)
@AutoConfigureMockMvc 
public class UserTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private TestRestTemplate testRestTemplate;
	
	private List<User> expectedUsers = new ArrayList<>();
	private ObjectMapper mapper = new ObjectMapper();
	
	@Before
	public void init() {
		
		userRepository.save(User.builder()
				.firstName("NAME1")
				.build());
		
		userRepository.save(User.builder()
				.firstName("NAME2")
				.build());
		
		userRepository.save(User.builder()
				.firstName("NAME3")
				.build());
	    
	    
		expectedUsers.addAll(userRepository.findAll());
		
		assertEquals(3, expectedUsers.size());
		
	}
	
	@Test
	public void testGetAllUsers_OK() {
		
		ResponseEntity<List<User>> userResponse = testRestTemplate.exchange("/skills-tracker/v1/users/", HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>(){});
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, userResponse.getHeaders().getContentType());
		assertEquals(expectedUsers, userResponse.getBody());
		
	}
	
	@Test
	public void testGetAllUsers_NOT_FOUND() throws IOException {
		
		userRepository.deleteAll();
		
		ResponseEntity<String> userResponse = testRestTemplate.getForEntity("/skills-tracker/v1/users/", String.class);
		
		JsonNode root = mapper.readTree(userResponse.getBody());
		
		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
		assertEquals("No users exist", root.path("message").asText());
		assertEquals(404, root.path("statusCode").asInt());
		
	}
	
	@Test
	public void testGetUserById_OK() {
		
		User expectedUser = expectedUsers.get(0);
		long id = expectedUser.getId();
		
		ResponseEntity<User> userResponse = testRestTemplate.getForEntity("/skills-tracker/v1/users/" + id, User.class);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, userResponse.getHeaders().getContentType());
		assertEquals(expectedUser, userResponse.getBody());
		
	}
	
	@Test
	public void testGetUserById_NOT_FOUND() throws IOException {
		
		long id = 99999L;
		
		ResponseEntity<String> userResponse = testRestTemplate.getForEntity("/skills-tracker/v1/users/" + id, String.class);
		
		JsonNode root = mapper.readTree(userResponse.getBody());
		
		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
		assertEquals("User not found for ID: " + id, root.path("message").asText());
		assertEquals(404, root.path("statusCode").asInt());
		
	}
	
	@After
	public void tearDown() {
		userRepository.deleteAll();
	}
	
}