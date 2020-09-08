package com.capgemini.paas.users_service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.capgemini.paas.users_service.persistence.dao.SkillRepository;
import com.capgemini.paas.users_service.persistence.dao.SkillUserLinkRepository;
import com.capgemini.paas.users_service.model.Skill;
import com.capgemini.paas.users_service.model.enums.Priority;
import com.capgemini.paas.users_service.model.enums.Proficiency;
import com.capgemini.paas.users_service.model.SkillUserLink;
import com.capgemini.paas.users_service.model.SkillUserLinkId;
import com.capgemini.paas.users_service.model.User;
import com.capgemini.paas.users_service.model.dto.SkillDTO;
import com.capgemini.paas.users_service.model.dto.UserDTO;
import com.capgemini.paas.users_service.model.enums.Role;
import com.capgemini.paas.users_service.persistence.dao.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UserApplication.class)
@AutoConfigureMockMvc 
public class UserTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SkillRepository skillRepository;
	
	@Autowired
	private SkillUserLinkRepository skillUserLinkRepository;
	
	@Autowired
    private TestRestTemplate testRestTemplate;
	
	private List<UserDTO> expectedUsers = new ArrayList<>();
	private ObjectMapper mapper = new ObjectMapper();

	private UserDTO badUserRequest;
	
	@Before
	public void init() {
		
		User user1 = userRepository.save(User.builder()
				.firstName("NAME2")
				.surname("SURNAME2")
				.role(Role.ADMIN)
				.lastUpdatedSkills(new Date().toString())
				.build());
		
		User user2 = userRepository.save(User.builder()
				.firstName("NAME2")
				.surname("SURNAME2")
				.role(Role.USER)
				.lastUpdatedSkills(new Date().toString())
				.build());
		
		User user3 = userRepository.save(User.builder()
				.firstName("NAME3")
				.surname("SURNAME3")
				.role(Role.ADMIN)
				.lastUpdatedSkills(new Date().toString())
				.build());
		
		Skill skill1 = skillRepository.save(Skill.builder()
				.name("NAME1")
				.priority(Priority.HIGH)
				.type("type")
				.build());
				
		Skill skill2 = skillRepository.save(Skill.builder()
				.name("NAME2")
				.priority(Priority.MEDIUM)
				.type("type")				
				.build());
		
		skillUserLinkRepository.save(SkillUserLink.builder()
				.skillUserLinkId(SkillUserLinkId.builder()
						.skillId(skill1.getSkillId())
						.userId(user1.getUserId())
						.build())
				.proficiency(Proficiency.EXPERIENCED)
				.skill(skill1)
				.build());
		
		skillUserLinkRepository.save(SkillUserLink.builder()
				.skillUserLinkId(SkillUserLinkId.builder()
						.skillId(skill2.getSkillId())
						.userId(user2.getUserId())
						.build())
				.proficiency(Proficiency.EXPERIENCED)
				.skill(skill2)
				.build());
	    
	    userRepository.findAll().forEach(user -> {
	    	expectedUsers.add(mapToDto(user));
	    });
	    
	    this.badUserRequest = UserDTO.builder()
	    		.userId(9999L)
	    		.firstName(null)
	    		.surname(null)
	    		.role(null)
	    		.lastUpdatedSkills(null)
	    		.skills(null)
	    		.build();
		
	}
	
	@Test
	public void testCreateUser_OK () throws IOException {
		
		UserDTO expectedUser = expectedUsers.get(0);
		expectedUser.setSkills(new ArrayList<SkillDTO>());
				
		HttpEntity<UserDTO> request = new HttpEntity<>(expectedUser);
		
		ResponseEntity<UserDTO> skillResponse = testRestTemplate.postForEntity("/skills-tracker/v1/users/", request, UserDTO.class);
										
		assertEquals(HttpStatus.CREATED, skillResponse.getStatusCode());
		assertEquals(expectedUser, skillResponse.getBody());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, skillResponse.getHeaders().getContentType());
	}
	
	@Test
	public void testCreateSkill_BAD_REQUEST () throws IOException {
				
		HttpEntity<UserDTO> request = new HttpEntity<>(badUserRequest);
		
		ResponseEntity<String> userResponse = testRestTemplate.postForEntity("/skills-tracker/v1/users/", request, String.class);
				
		JsonNode root = mapper.readTree(userResponse.getBody());
						
		assertEquals(HttpStatus.BAD_REQUEST, userResponse.getStatusCode());
		assertEquals(400, root.path("statusCode").asInt());
	}
	
	@Test
	public void testGetAllUsers_OK() {
		
		ResponseEntity<List<UserDTO>> userResponse = testRestTemplate.exchange("/skills-tracker/v1/users/", HttpMethod.GET, null, new ParameterizedTypeReference<List<UserDTO>>(){});
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, userResponse.getHeaders().getContentType());
		assertEquals(expectedUsers, userResponse.getBody());
		
	}
	
	@Test
	public void testGetAllUsers_NOT_FOUND() throws IOException {
		
		tearDown();
		
		ResponseEntity<String> userResponse = testRestTemplate.getForEntity("/skills-tracker/v1/users/", String.class);
		
		JsonNode root = mapper.readTree(userResponse.getBody());
		
		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
		assertEquals("No users exist", root.path("message").asText());
		assertEquals(404, root.path("statusCode").asInt());
		
	}
	
	@Test
	public void testGetUserById_OK() {
		
		UserDTO expectedUser = expectedUsers.get(0);
		long id = expectedUser.getUserId();
		
		ResponseEntity<UserDTO> userResponse = testRestTemplate.getForEntity("/skills-tracker/v1/users/" + id, UserDTO.class);
		
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
	
	@Test
	public void testUpdateUserById_OK () throws IOException {
		
		UserDTO expectedUser = expectedUsers.get(0);
		long id = expectedUser.getUserId();
		expectedUser.setFirstName("newName");
		HttpEntity<UserDTO> request = new HttpEntity<>(expectedUser);
		
		ResponseEntity<UserDTO> userResponse = testRestTemplate.exchange("/skills-tracker/v1/users/" + id, HttpMethod.PUT, request, UserDTO.class);
				
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, userResponse.getHeaders().getContentType());
		assertEquals(expectedUser, userResponse.getBody());
	}
	
	@Test
	public void testUpdateUserById_BAD_REQUEST () throws IOException {
		
		long id = badUserRequest.getUserId();
		
		HttpEntity<UserDTO> request = new HttpEntity<>(badUserRequest);
		
		ResponseEntity<String> userResponse = testRestTemplate.exchange("/skills-tracker/v1/users/" + id, HttpMethod.PUT, request, String.class);
		
		JsonNode root = mapper.readTree(userResponse.getBody());
						
		assertEquals(HttpStatus.BAD_REQUEST, userResponse.getStatusCode());
		assertEquals(400, root.path("statusCode").asInt());
	}
	
	@Test
	public void testUpdateUserById_NOT_FOUND () throws IOException {
		UserDTO expectedUser = expectedUsers.get(0);
		long id = 99999L;
		expectedUser.setFirstName("newName");
		HttpEntity<UserDTO> request = new HttpEntity<>(expectedUser);
		
		ResponseEntity<String> skillResponse = testRestTemplate.exchange("/skills-tracker/v1/users/" + id, HttpMethod.PUT, request, String.class);
		
		JsonNode root = mapper.readTree(skillResponse.getBody());
						
		assertEquals(HttpStatus.NOT_FOUND, skillResponse.getStatusCode());
		assertEquals("User not found for ID: " + id, root.path("message").asText());
		assertEquals(404, root.path("statusCode").asInt());
	}
	
	@Test
	public void testDeleteUserById_OK () throws IOException {
		
		UserDTO expectedUser = expectedUsers.get(0);

		long id = expectedUser.getUserId();
		
		System.out.println("hit2");
		System.out.println(id);
				
		ResponseEntity<String> userResponse = testRestTemplate.exchange("/skills-tracker/v1/users/" + id, HttpMethod.DELETE, null, String.class);
		
		//Check status code 200, OK
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		
		ResponseEntity<String> getResponse = testRestTemplate.getForEntity("/skills-tracker/v1/users/" + id, String.class);
		
		JsonNode root = mapper.readTree(getResponse.getBody());
		
		//Check User was deleted
		assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
		assertEquals("User not found for ID: " + id, root.path("message").asText());
		assertEquals(404, root.path("statusCode").asInt());
		
	}
	
	@Test
	public void testDeleteUserById_NOT_FOUND () throws IOException {
		
		long id = 99999L;
				
		ResponseEntity<String> skillResponse = testRestTemplate.exchange("/skills-tracker/v1/users/" + id, HttpMethod.DELETE, null, String.class);
				
		JsonNode root = mapper.readTree(skillResponse.getBody());
		
		assertEquals(HttpStatus.NOT_FOUND, skillResponse.getStatusCode());
		assertEquals("User not found for ID: " + id, root.path("message").asText());
		assertEquals(404, root.path("statusCode").asInt());
		
	}
	
	@After
	public void tearDown() {
		skillUserLinkRepository.deleteAll();
		skillRepository.deleteAll();
		userRepository.deleteAll();
		expectedUsers.clear();
	}
	
	public UserDTO mapToDto (User user) {
		
		List<SkillDTO> skillsDto = new ArrayList<SkillDTO>();
		
		if(user.getSkillUserLink() != null) {
		
			user.getSkillUserLink().forEach(link -> {
		
				Skill skill = link.getSkill();
				
				skillsDto.add(SkillDTO.builder()
						.skillId(skill.getSkillId())
						.name(skill.getName())
						.type(skill.getType())
						.priority(skill.getPriority())
						.proficiency(link.getProficiency())
						.build());
				
			});
		}
		
		UserDTO userDto = UserDTO.builder()
				.userId(user.getUserId())
				.firstName(user.getFirstName())
				.surname(user.getSurname())
				.role(user.getRole())
				.lastUpdatedSkills(user.getLastUpdatedSkills())
				.skills(skillsDto)
				.build();

		return userDto;
	
	}
	
}