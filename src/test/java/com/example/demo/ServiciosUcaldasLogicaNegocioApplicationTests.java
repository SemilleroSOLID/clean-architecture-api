package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Checks that every bean of the application can be created. Profile "test": no SQL Server or RabbitMQ needed (#24).
@SpringBootTest
@ActiveProfiles("test")
class ServiciosUcaldasLogicaNegocioApplicationTests {

	@Test
	void contextLoads() {
	}

}
