package com.capgemini.paas.users_service.model.dto;

import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.capgemini.paas.users_service.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter @Setter @ToString @NoArgsConstructor @EqualsAndHashCode @AllArgsConstructor @Builder
@DynamicUpdate
public class UserDTO {
	
	private long userId;
	private String firstName;
	private String surname;
	private Role role;
	private String lastUpdatedSkills;
	private List<SkillDTO> skills;
	
}
