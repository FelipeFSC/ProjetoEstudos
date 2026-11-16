package com.minimercado.pedidos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:pedidos-teste")
class PedidosApplicationTests {

	@Test
	void contextLoads() {
	}

}
