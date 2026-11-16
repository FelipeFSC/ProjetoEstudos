package com.minimercado.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.minimercado.pedidos.model.ItemPedido;
import com.minimercado.pedidos.model.Pedido;
import com.minimercado.pedidos.model.StatusPedido;
import com.minimercado.pedidos.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

class PedidoServiceTests {

	private static final String CATALOGO = "http://catalogo/api/produtos";

	private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
	private MockRestServiceServer server;
	private PedidoService pedidoService;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		pedidoService = new PedidoService(pedidoRepository, builder, "http://catalogo");
		when(pedidoRepository.save(any())).thenAnswer((invocation) -> invocation.getArgument(0));
	}

	@Test
	void criaPedidoComPrecoDoCatalogoEBaixaOEstoque() {
		expectProduto(1, "Arroz 5kg", "27.90");
		expectEstoque(1, "baixa-estoque", 2);
		expectProduto(4, "Leite 1L", "5.29");
		expectEstoque(4, "baixa-estoque", 6);

		Pedido pedido = pedidoService.criar("Maria",
				List.of(new PedidoService.Item(1L, 2), new PedidoService.Item(4L, 6)));

		assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CRIADO);
		assertThat(pedido.getItens()).hasSize(2);
		assertThat(pedido.getTotal()).isEqualByComparingTo("87.54");
		verify(pedidoRepository).save(pedido);
		server.verify();
	}

	@Test
	void devolveOEstoqueQuandoUmItemFalha() {
		expectProduto(1, "Arroz 5kg", "27.90");
		expectEstoque(1, "baixa-estoque", 1);
		expectProduto(5, "Queijo", "24.90");
		server.expect(requestTo(CATALOGO + "/5/baixa-estoque"))
			.andRespond(withStatus(HttpStatus.UNPROCESSABLE_CONTENT).contentType(MediaType.APPLICATION_JSON)
				.body("{\"status\":422,\"message\":\"Estoque insuficiente para Queijo\"}"));
		expectEstoque(1, "repor-estoque", 1);

		assertThatThrownBy(() -> pedidoService.criar("João",
				List.of(new PedidoService.Item(1L, 1), new PedidoService.Item(5L, 999))))
			.isInstanceOfSatisfying(ResponseStatusException.class, (ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
				assertThat(ex.getReason()).isEqualTo("Estoque insuficiente para Queijo");
			});
		verify(pedidoRepository, never()).save(any());
		server.verify();
	}

	@Test
	void informaCatalogoForaDoArComoBadGateway() {
		server.expect(requestTo(CATALOGO + "/1")).andRespond(withServerError());

		assertThatThrownBy(() -> pedidoService.criar("Ana", List.of(new PedidoService.Item(1L, 1))))
			.isInstanceOfSatisfying(ResponseStatusException.class,
					(ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY));
		verify(pedidoRepository, never()).save(any());
	}

	@Test
	void cancelarDevolveOEstoque() {
		Pedido pedido = new Pedido("Maria");
		pedido.adicionarItem(new ItemPedido(1L, "Arroz 5kg", new BigDecimal("27.90"), 2));
		when(pedidoRepository.findWithItensById(7L)).thenReturn(Optional.of(pedido));
		expectEstoque(1, "repor-estoque", 2);

		assertThat(pedidoService.cancelar(7L).getStatus()).isEqualTo(StatusPedido.CANCELADO);
		server.verify();
	}

	private void expectProduto(long id, String nome, String preco) {
		server.expect(requestTo(CATALOGO + "/" + id))
			.andRespond(withSuccess("{\"id\":%d,\"nome\":\"%s\",\"preco\":%s,\"estoque\":100}".formatted(id, nome, preco),
					MediaType.APPLICATION_JSON));
	}

	private void expectEstoque(long id, String operacao, int quantidade) {
		server.expect(requestTo(CATALOGO + "/" + id + "/" + operacao))
			.andExpect(method(HttpMethod.POST))
			.andExpect(content().json("{\"quantidade\":%d}".formatted(quantidade)))
			.andRespond(withSuccess());
	}

}
