package com.capgemini.paas.users_service.model.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;

import com.capgemini.paas.users_service.model.enums.Role;
import com.capgemini.paas.users_service.model.validation.ObjectValidator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter @Setter @ToString @NoArgsConstructor @EqualsAndHashCode @AllArgsConstructor @Builder
@DynamicUpdate
public class UserDTO implements ObjectValidator {
	
	@NotNull
	private long userId;
	@NotNull
	private String firstName;
	@NotNull
	private String surname;
	@NotNull
	private Role role;
	private String lastUpdatedSkills;
	private List<SkillDTO> skills;
	
}
