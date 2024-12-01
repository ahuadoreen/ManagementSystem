package com.example.ms.security;

import com.example.ms.security.entity.User;
import com.example.ms.security.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class SecurityModuleApplicationTests {
	@Autowired
	UserService userService;

	@Test
	void testAddBatch() {
		System.out.println(LocalDateTime.now());
		List<User> newUsers = new ArrayList<>();
		for (int i = 0; i < 100000; i ++) {
			User newUser = new User();
//			newUser.setUsername("user" + (i + 1));
			newUser.setPassword("123456");
			newUser.setDisplayName("User" + (i + 1));
			newUser.setEnable(true);
			newUsers.add(newUser);
		}
		userService.addBatch(newUsers);
		System.out.println(LocalDateTime.now());
	}

	@Test
	void testUpdateBatch() {
		System.out.println(LocalDateTime.now());
		List<User> newUsers = new ArrayList<>();
		for (int i = 0; i < 100000; i ++) {
			User newUser = new User();
			newUser.setId((long) (i + 1));
			newUser.setUsername("user" + (i + 1));
			newUser.setPassword("123456");
			newUser.setDisplayName("Test" + (i + 1));
			newUser.setEnable(true);
			newUsers.add(newUser);
		}
		userService.updateMultiple(newUsers);
		System.out.println(LocalDateTime.now());
	}
}
