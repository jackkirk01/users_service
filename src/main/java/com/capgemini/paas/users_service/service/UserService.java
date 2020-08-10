package com.capgemini.paas.users_service.service;

import java.util.List;
import java.util.Optional;

import com.capgemini.paas.users_service.model.User;
import com.capgemini.paas.users_service.model.dto.UserDTO;

public interface UserService {
	
	List<UserDTO> getUsers();
	
	Optional<UserDTO> getUserById(long id);

	UserDTO saveUser(User user);

	UserDTO updateUser(UserDTO user);

	void deleteUser(long id);

	
}