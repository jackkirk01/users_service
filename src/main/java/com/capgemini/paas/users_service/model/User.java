package com.capgemini.paas.users_service.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

@Entity
@Getter @Setter @ToString @NoArgsConstructor @EqualsAndHashCode @AllArgsConstructor @Builder
@DynamicUpdate
@Table(name = "USER")
public class User implements ObjectValidator {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="USER_ID")
	private long userId;
	
	@Size(max=20)
	@NotNull
	@Column(name="FIRST_NAME", nullable = false)
	private String firstName;

	@Size(max=20)
	@NotNull
	@Column(name="SURNAME", nullable = false)
	private String surname;
	
	@NotNull
	@Column (name="ROLE", nullable = false)
	private Role role;
	
	@Size(max=100)
	@Column (name="LAST_UPDATED_SKILLS")
	private String lastUpdatedSkills;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<SkillUserLink> skillUserLink;
}
