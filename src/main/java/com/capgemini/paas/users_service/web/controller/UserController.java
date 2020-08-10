package com.capgemini.paas.users_service.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.paas.users_service.model.User;
import com.capgemini.paas.users_service.model.dto.UserDTO;
import com.capgemini.paas.users_service.service.UserService;

import com.capgemini.paas.services.errorhandling.persistence.DataNotFoundException;
import com.capgemini.paas.services.errorhandling.web.BadRequestException;
import com.capgemini.paas.users_service.model.Skill;
import com.capgemini.paas.users_service.model.SkillUserLink;
import com.capgemini.paas.users_service.model.SkillUserLinkId;
import com.capgemini.paas.users_service.model.enums.Priority;
import com.capgemini.paas.users_service.model.enums.Proficiency;
import com.capgemini.paas.users_service.persistence.dao.SkillRepository;
import com.capgemini.paas.users_service.persistence.dao.SkillUserLinkRepository;
import com.capgemini.paas.users_service.persistence.dao.UserRepository;
import com.capgemini.paas.services.commonutility.ServiceProperties;

@RestController
@RequestMapping("/skills-tracker/v1/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	SkillRepository skillRepository;
	
	@Autowired
	SkillUserLinkRepository skillUserLinkRepository;
	
	public UserController() {
	     super();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<UserDTO>> retrieveUsers() {
		
		List<UserDTO> users = userService.getUsers();
		
		if (users.size() == 0) {
			throw new DataNotFoundException("No users exist");
		} else {
			return new ResponseEntity<List<UserDTO>>(users, ServiceProperties.generateBaggageHeaders(), HttpStatus.OK);
		}
		
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<UserDTO> createSkill(@RequestBody User user) {
				
		if(!user.validate()) {
			throw new BadRequestException("Invalid input object.");
		}
		
		skillRepository.save(Skill.builder()
				.id(1)
				.name("Java EE")
				.priority(Priority.HIGH)
				.type("Programming Language")
				.build());
		
//		userService.saveUser(user);
//				
//		user.setSkillUserLink(List.of(
//				skillUserLinkRepository.save(SkillUserLink.builder()
//						.skillUserLinkId(SkillUserLinkId.builder()
//								.skillId(1L)
//								.userId(1L)
//								.build())
//						.proficiency(Proficiency.EXPERIENCED)
//						.skill(skillRepository.save(Skill.builder()
//								.id(1)
//								.name("Java EE")
//								.priority(Priority.HIGH)
//								.type("Programming Language")
//								.build()))
//						.build())
//				,
//				skillUserLinkRepository.save(SkillUserLink.builder()
//						.skillUserLinkId(SkillUserLinkId.builder()
//								.skillId(2L)
//								.userId(1L)
//								.build())
//						.proficiency(Proficiency.EXPERT)
//						.skill(skillRepository.save(Skill.builder()
//								.id(2)
//								.name("Java SE")
//								.priority(Priority.LOW)
//								.type("Programming Language")
//								.build()))
//						.build())
//				));
		
		return new ResponseEntity<UserDTO>(userService.saveUser(user), ServiceProperties.generateBaggageHeaders(), HttpStatus.CREATED);
	
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<UserDTO> updateSkill(@RequestBody UserDTO user) {
		
//		if(!userDTO.validate()) {
//			throw new BadRequestException();
//		}
//		
//		if (skills.size() == 0) {
//			throw new DataNotFoundException("No skills exist");
//		} else {
		return new ResponseEntity<UserDTO>(userService.updateUser(user), ServiceProperties.generateBaggageHeaders(), HttpStatus.OK);
//		}
		
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<UserDTO> retrieveUserBasedOnId(@PathVariable("id") long id) {
		
		Optional<UserDTO> userDto = userService.getUserById(id);
		
		if (!userDto.isPresent()) {
			throw new DataNotFoundException("User not found for ID: " + id);
		} else {
			return new ResponseEntity<UserDTO>(userDto.get(), ServiceProperties.generateBaggageHeaders(), HttpStatus.OK);
		}
		
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteUser(@PathVariable("id") long id) {
		
		userService.deleteUser(id);
		
	}

}
