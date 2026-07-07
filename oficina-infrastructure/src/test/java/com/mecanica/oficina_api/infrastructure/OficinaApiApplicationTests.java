package com.mecanica.oficina_api.infrastructure;

import com.mecanica.oficina_api.infrastructure.integration.PostgresTestcontainersSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OficinaApiApplicationTests extends PostgresTestcontainersSupport {

	@Test
	void contextLoads() {
	}

}
