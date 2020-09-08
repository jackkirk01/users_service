package com.capgemini.paas.users_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.paas.users_service.model.Skill;
import com.capgemini.paas.users_service.model.SkillUserLink;
import com.capgemini.paas.users_service.model.SkillUserLinkId;
import com.capgemini.paas.users_service.model.dto.SkillDTO;
import com.capgemini.paas.users_service.model.User;
import com.capgemini.paas.users_service.model.dto.UserDTO;
import com.capgemini.paas.users_service.persistence.dao.SkillUserLinkRepository;
import com.capgemini.paas.users_service.persistence.dao.UserRepository;
import com.capgemini.paas.users_service.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SkillUserLinkRepository skillUserLinkRepository;
	
	@Override
	public List<UserDTO> getUsers() {
		
		List<UserDTO> usersDto = new ArrayList<UserDTO>();
		
		userRepository.findAll().forEach(user -> usersDto.add(mapToDto(user)));
		
		return usersDto;
	}
	
	@Override
	public Optional<UserDTO> getUserById(long id) {
		
		Optional<User> user = userRepository.findById(id);
		
		Optional<UserDTO> userDto = Optional.empty();
		
		if(user.isPresent()) {
			
			userDto = Optional.of(mapToDto(user.get()));
			
		}
		
		return userDto;
	}

	@Override
	public UserDTO saveUser(User user) {
				
		return mapToDto(userRepository.save(user));
	}
	
	@Override
	public UserDTO updateUser(UserDTO userDto) {
		
		// Remove existing skills to recreate any new ones.
		// By default this handles the removal of any skills if they are not present in the userDto.
		deleteExistingSkills(userDto);
		
		List<SkillUserLink> links = new ArrayList<SkillUserLink>();
		
		if(userDto.getSkills() != null) {
			
			userDto.getSkills().forEach(skillDto -> {
				Skill skill = Skill.builder()
						.skillId(skillDto.getSkillId())
						.name(skillDto.getName())
						.priority(skillDto.getPriority())
						.type(skillDto.getType())
						.build();
				
				links.add(SkillUserLink.builder()
						.skillUserLinkId(SkillUserLinkId.builder()
								.userId(userDto.getUserId())
								.skillId(skill.getSkillId())
								.build())
						.skill(skill)
						.user(null)
						.proficiency(skillDto.getProficiency())
						.build());
			});
		}
		
		User user = User.builder()
			.userId(userDto.getUserId())
			.firstName(userDto.getFirstName())
			.surname(userDto.getSurname())
			.lastUpdatedSkills(userDto.getLastUpdatedSkills())
			.role(userDto.getRole())
			.build();
		
		userRepository.save(user);
		
		links.forEach(link -> skillUserLinkRepository.save(link));
		
		user.setSkillUserLink(links);
		
		return mapToDto(user);
	}

	@Override
	public void deleteUser(long id) {
		userRepository.deleteById(id);
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
	
	public void deleteExistingSkills (UserDTO userDto) {
		
		Optional<User> user = userRepository.findById(userDto.getUserId());
		
		if(!user.isEmpty()) {
			List<SkillUserLink> links = user.get().getSkillUserLink();
			
			links.forEach(link -> {
				
				SkillUserLinkId linkId = SkillUserLinkId.builder()
					.skillId(link.getSkill().getSkillId())
					.userId(userDto.getUserId())
					.build();
				
				skillUserLinkRepository.deleteById(linkId);
				
			});
			
		}
	}
	
}
