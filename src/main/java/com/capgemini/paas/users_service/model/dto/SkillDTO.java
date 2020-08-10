package com.capgemini.paas.users_service.model.dto;

import org.hibernate.annotations.DynamicUpdate;

import com.capgemini.paas.users_service.model.enums.Priority;
import com.capgemini.paas.users_service.model.enums.Proficiency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @Setter @ToString @NoArgsConstructor @EqualsAndHashCode @AllArgsConstructor @Builder
@DynamicUpdate
public class SkillDTO {
	
	private long skillId;
	private String name;
	private String type;
	private Priority priority;
	private Proficiency proficiency;
}
