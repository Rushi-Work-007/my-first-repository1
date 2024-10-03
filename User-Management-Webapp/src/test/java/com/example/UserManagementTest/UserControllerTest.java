package com.example.UserManagementTest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.usermanagement.controller.UserController;
import com.example.usermanagement.model.User;
import com.example.usermanagement.service.UserService;

@SpringBootTest(classes = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testListUsers() throws Exception {
		when(userService.searchUsersByLastName(anyString())).thenReturn(List.of(new User(1L, "John", "Cena")));

		mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(view().name("user-list"))
				.andExpect(model().attribute("users", hasSize(1))).andExpect(model().attribute("lastName", ""));
	}

	@Test
	public void testRegisterUser() throws Exception {
		User user = new User(1L, "John", "Cena");
		mockMvc.perform(post("/users/register").flashAttr("user", user)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users"))
				.andExpect(flash().attribute("message", "User registered successfully."));

		verify(userService, times(1)).saveUser(any(User.class));
	}

	@Test
	public void testEditUserForm() throws Exception {
		User user = new User(1L, "John", "Cena");
		when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

		mockMvc.perform(get("/users/edit/1")).andExpect(status().isOk()).andExpect(view().name("edit"))
				.andExpect(model().attribute("user", user));
	}

	@Test
	public void testEditUserForm_UserNotFound() throws Exception {
		when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

		mockMvc.perform(get("/users/edit/1")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/users"))
				.andExpect(flash().attribute("error", "User not found."));
	}

	@Test
	public void testUpdateUser() throws Exception {
		User user = new User(1L, "John", "Cena");
		mockMvc.perform(post("/users/update").flashAttr("user", user)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users"))
				.andExpect(flash().attribute("message", "User updated successfully."));

		verify(userService, times(1)).saveUser(any(User.class));
	}

	@Test
	public void testShowConfirmDeletePage_UserNotFound() throws Exception {
		when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

		mockMvc.perform(get("/users/confirm-delete").param("userId", "1")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users")).andExpect(flash().attribute("error", "User not found."));
	}

	@Test
	public void testDeleteUser() throws Exception {
		when(userService.getUserById(anyLong())).thenReturn(Optional.of(new User(1L, "John", "Cena")));

		mockMvc.perform(post("/users/delete").param("userId", "1")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users"))
				.andExpect(flash().attribute("message", "User deleted successfully."));

		verify(userService, times(1)).deleteUser(anyLong());
	}

	@Test
	public void testDeleteUser_UserNotFound() throws Exception {
		when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

		mockMvc.perform(post("/users/delete").param("userId", "1")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users")).andExpect(flash().attribute("error", "User not found."));
	}
}
