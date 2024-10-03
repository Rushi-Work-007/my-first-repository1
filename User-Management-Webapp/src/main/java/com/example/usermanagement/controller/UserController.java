package com.example.usermanagement.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.usermanagement.model.User;
import com.example.usermanagement.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // List all users or search by last name
    @GetMapping
    public String listUsers(@RequestParam(name = "lastName", required = false, defaultValue = "") String lastName,
            Model model) {
        logger.info("Fetching users with last name: {}", lastName);
        model.addAttribute("users", userService.searchUsersByLastName(lastName));
        model.addAttribute("lastName", lastName);
        return "user-list";
    }

    // Display user registration form
    @GetMapping("/register")
    public String registerUserForm(Model model) {
        logger.info("Displaying user registration form.");
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle user registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        logger.info("Registering new user: {}", user);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "User registered successfully.");
        return "redirect:/users";
    }

    // Display user edit form
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying edit form for user with id: {}", id);
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "edit";
        }
        logger.warn("User with id {} not found.", id);
        redirectAttributes.addFlashAttribute("error", "User not found.");
        return "redirect:/users";
    }

    // Handle user update
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        if (user.getId() == null) {
            logger.error("Attempted to update user with null ID.");
            redirectAttributes.addFlashAttribute("error", "No user selected for update.");
            return "redirect:/users";
        }
        logger.info("Updating user: {}", user);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "User updated successfully.");
        return "redirect:/users";
    }

    // Show confirmation page before deleting user
    @GetMapping("/confirm-delete")
    public String showConfirmDeletePage(@RequestParam("userId") Long userId, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying confirmation page for deletion of user with id: {}", userId);
        Optional<User> user = userService.getUserById(userId);

        if (!user.isPresent()) {
            logger.warn("User with id {} not found for deletion.", userId);
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/users";
        }

        model.addAttribute("user", user.get());
        return "confirm-delete";
    }

    // Handle user deletion
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        logger.info("Deleting user with id: {}", userId);
        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            logger.warn("User with id {} not found for deletion.", userId);
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/users";
        }

        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully.");
        return "redirect:/users";
    }

    // Show selected users based on IDs
    @GetMapping("/show")
    public String showUsers(@RequestParam("userIds") List<Long> userIds, Model model) {
        logger.info("Fetching users with IDs: {}", userIds);
        List<User> users = userIds.stream()
                .map(id -> userService.getUserById(id).orElse(null))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "show";
    }
}
