package com.example.usermanagement.service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	// Search users by last name
	public List<User> searchUsersByLastName(String lastName) {
		logger.info("Searching users with last name containing: {}", lastName);
		List<User> users = userRepository.findByLastNameContaining(lastName);
		logger.info("Found {} users with last name containing: {}", users.size(), lastName);
		return users;
	}

	// Get a user by ID
	public Optional<User> getUserById(Long id) {
		logger.info("Fetching user with ID: {}", id);
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			logger.info("User found: {}", user.get());
		} else {
			logger.warn("User with ID {} not found.", id);
		}
		return user;
	}

	// Save or update a user
	public void saveUser(User user) {
		logger.info("Saving or updating user: {}", user);
		userRepository.save(user);
		logger.info("User saved or updated successfully.");
	}

	// Delete a user by ID
	public void deleteUser(Long id) {
		logger.info("Deleting user with ID: {}", id);
		if (userRepository.existsById(id)) {
			userRepository.deleteById(id);
			logger.info("User with ID {} deleted successfully.", id);
		} else {
			logger.warn("User with ID {} does not exist.", id);
		}
	}

	// Find users by a list of IDs
	public List<User> findUsersByIds(List<Long> ids) {
		logger.info("Fetching users with IDs: {}", ids);
		List<User> users = userRepository.findAllById(ids);
		logger.info("Found {} users for the given IDs.", users.size());
		return users;
	}

	// Bulk delete users by IDs
	public void deleteUsersByIds(List<Long> ids) {
		if (ids != null && !ids.isEmpty()) {
			logger.info("Deleting users with IDs: {}", ids);
			userRepository.deleteAllById(ids);
			logger.info("Users with IDs {} deleted successfully.", ids);
		} else {
			logger.warn("No IDs provided for bulk delete.");
		}
	}

	// Find user by ID, returns null if not found
	public User findById(Long userId) {
		logger.info("Finding user by ID: {}", userId);
		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
			logger.info("User found: {}", user);
		} else {
			logger.warn("User with ID {} not found.", userId);
		}
		return user;
	}

	// Delete user by ID with existence check
	public void deleteById(Long userId) {
		logger.info("Attempting to delete user by ID: {}", userId);
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
			logger.info("User with ID {} deleted successfully.", userId);
		} else {
			logger.error("User with ID {} does not exist, cannot delete.", userId);
			throw new RuntimeException("User with ID " + userId + " does not exist.");
		}
	}
}
