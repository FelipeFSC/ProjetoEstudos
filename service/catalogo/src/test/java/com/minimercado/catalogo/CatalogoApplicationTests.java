package com.minimercado.catalogo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:catalogo-teste")
class CatalogoApplicationTests {

	@Test
	void contextLoads() {
	}

}
