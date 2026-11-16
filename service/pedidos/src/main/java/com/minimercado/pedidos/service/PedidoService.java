package com.minimercado.pedidos.service;

import com.minimercado.pedidos.model.ItemPedido;
import com.minimercado.pedidos.model.Pedido;
import com.minimercado.pedidos.model.StatusPedido;
import com.minimercado.pedidos.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

	private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

	private final PedidoRepository pedidoRepository;
	private final RestClient restClient;
	private final String catalogoUrl;

	public PedidoService(PedidoRepository pedidoRepository, RestClient.Builder restClientBuilder,
			@Value("${mini-mercado.catalogo-url}") String catalogoUrl) {
		this.pedidoRepository = pedidoRepository;
		this.restClient = restClientBuilder.build();
		this.catalogoUrl = catalogoUrl;
	}

	public List<Pedido> listar() {
		return pedidoRepository.findAllByOrderByIdDesc();
	}

	public Pedido buscar(Long id) {
		return pedidoRepository.findWithItensById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado: " + id));
	}

	/**
	 * Cria o pedido consultando o preço e dando baixa no estoque do catálogo.
	 * Catálogo e pedidos têm bancos separados, então não existe uma transação única: se a baixa de um
	 * item falhar, devolvemos o estoque dos itens que já tinham sido baixados.
	 */
	public Pedido criar(String cliente, List<Item> itens) {
		Pedido pedido = new Pedido(cliente);
		List<Item> itensBaixados = new ArrayList<>();
		try {
			for (Item item : itens) {
				ProdutoCatalogo produto = buscarProduto(item.produtoId());
				alterarEstoque(item.produtoId(), "baixa-estoque", item.quantidade());
				itensBaixados.add(item);
				pedido.adicionarItem(
						new ItemPedido(produto.id(), produto.nome(), produto.preco(), item.quantidade()));
			}
			Pedido salvo = pedidoRepository.save(pedido);
			log.info("Pedido {} criado para {} com total {}", salvo.getId(), cliente, salvo.getTotal());
			return salvo;
		}
		catch (RuntimeException ex) {
			devolverEstoque(itensBaixados);
			throw ex;
		}
	}

	public Pedido cancelar(Long id) {
		Pedido pedido = buscar(id);
		if (pedido.getStatus() == StatusPedido.CANCELADO) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "O pedido " + id + " já está cancelado");
		}
		for (ItemPedido item : pedido.getItens()) {
			alterarEstoque(item.getProdutoId(), "repor-estoque", item.getQuantidade());
		}
		pedido.setStatus(StatusPedido.CANCELADO);
		log.info("Pedido {} cancelado", id);
		return pedidoRepository.save(pedido);
	}

	private void devolverEstoque(List<Item> itens) {
		for (Item item : itens) {
			try {
				alterarEstoque(item.produtoId(), "repor-estoque", item.quantidade());
			}
			catch (RuntimeException ex) {
				log.error("Falha ao devolver {} unidade(s) do produto {}", item.quantidade(), item.produtoId(), ex);
			}
		}
	}

	private ProdutoCatalogo buscarProduto(Long produtoId) {
		try {
			return restClient.get()
				.uri(catalogoUrl + "/api/produtos/{id}", produtoId)
				.retrieve()
				.body(ProdutoCatalogo.class);
		}
		catch (RestClientResponseException ex) {
			throw erroDoCatalogo(ex);
		}
		catch (RestClientException ex) {
			log.error("Falha ao consultar o produto {} no catálogo", produtoId, ex);
			throw catalogoIndisponivel(ex);
		}
	}

	/** Chama a baixa ou a reposição de estoque no catálogo. */
	private void alterarEstoque(Long produtoId, String operacao, int quantidade) {
		try {
			restClient.post()
				.uri(catalogoUrl + "/api/produtos/{id}/" + operacao, produtoId)
				.body(Map.of("quantidade", quantidade))
				.retrieve()
				.toBodilessEntity();
		}
		catch (RestClientResponseException ex) {
			throw erroDoCatalogo(ex);
		}
		catch (RestClientException ex) {
			log.error("Falha na operação {} do produto {}", operacao, produtoId, ex);
			throw catalogoIndisponivel(ex);
		}
	}

	/** Repassa para o front os erros 4xx do catálogo (ex.: 404, estoque insuficiente) com a mesma mensagem. */
	private ResponseStatusException erroDoCatalogo(RestClientResponseException ex) {
		if (!ex.getStatusCode().is4xxClientError()) {
			log.error("O catálogo respondeu com erro {}", ex.getStatusCode(), ex);
			return catalogoIndisponivel(ex);
		}
		String mensagem = "Erro no catálogo";
		try {
			ErroCatalogo erro = ex.getResponseBodyAs(ErroCatalogo.class);
			if (erro != null && erro.message() != null) {
				mensagem = erro.message();
			}
		}
		catch (RuntimeException ignorada) {
			// O corpo da resposta não estava no formato esperado; fica a mensagem genérica.
		}
		return new ResponseStatusException(ex.getStatusCode(), mensagem, ex);
	}

	private ResponseStatusException catalogoIndisponivel(Exception ex) {
		return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível acessar o catálogo", ex);
	}

	public record Item(Long produtoId, int quantidade) {
	}

	private record ProdutoCatalogo(Long id, String nome, BigDecimal preco) {
	}

	private record ErroCatalogo(String message) {
	}

}
