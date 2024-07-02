package com.example.Todo_list;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class TodoListApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("Active profile: " + System.getProperty("spring.profiles.active"));
	}

}
