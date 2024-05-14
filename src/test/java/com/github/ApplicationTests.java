package com.github;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

	@Test
	@DisplayName("Deve carregar o contexto da aplicação com sucesso")
	void contextLoads() {
	}

}
