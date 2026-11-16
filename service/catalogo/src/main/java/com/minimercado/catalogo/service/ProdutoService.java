package com.minimercado.catalogo.service;

import com.minimercado.catalogo.model.Produto;
import com.minimercado.catalogo.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProdutoService {

	private final ProdutoRepository produtoRepository;

	public ProdutoService(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	public List<Produto> listar(String categoria) {
		if (categoria == null || categoria.isBlank()) {
			return produtoRepository.findAllByOrderByNome();
		}
		return produtoRepository.findByCategoriaIgnoreCaseOrderByNome(categoria);
	}

	public Produto buscar(Long id) {
		return produtoRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + id));
	}

	public Produto criar(Produto produto) {
		return produtoRepository.save(produto);
	}

	public Produto atualizar(Long id, Produto dados) {
		Produto produto = buscar(id);
		produto.setNome(dados.getNome());
		produto.setDescricao(dados.getDescricao());
		produto.setCategoria(dados.getCategoria());
		produto.setPreco(dados.getPreco());
		produto.setEstoque(dados.getEstoque());
		return produtoRepository.save(produto);
	}

	public void remover(Long id) {
		produtoRepository.delete(buscar(id));
	}

	/** Chamado pelo serviço de pedidos quando um pedido é criado. */
	@Transactional
	public Produto baixarEstoque(Long id, int quantidade) {
		Produto produto = buscar(id);
		if (quantidade > produto.getEstoque()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
					"Estoque insuficiente para %s: disponível %d, solicitado %d"
						.formatted(produto.getNome(), produto.getEstoque(), quantidade));
		}
		produto.setEstoque(produto.getEstoque() - quantidade);
		return produtoRepository.save(produto);
	}

	/** Chamado pelo serviço de pedidos quando um pedido é cancelado. */
	@Transactional
	public Produto reporEstoque(Long id, int quantidade) {
		Produto produto = buscar(id);
		produto.setEstoque(produto.getEstoque() + quantidade);
		return produtoRepository.save(produto);
	}

	/** Cadastra alguns produtos na primeira vez que o serviço sobe com o banco vazio. */
	@EventListener(ApplicationReadyEvent.class)
	public void cadastrarProdutosIniciais() {
		if (produtoRepository.count() > 0) {
			return;
		}
		produtoRepository.saveAll(List.of(
				new Produto("Arroz 5kg", "Arroz branco tipo 1", "Mercearia", new BigDecimal("27.90"), 50),
				new Produto("Feijão 1kg", "Feijão carioca", "Mercearia", new BigDecimal("8.49"), 80),
				new Produto("Café 500g", "Café torrado e moído", "Mercearia", new BigDecimal("18.90"), 40),
				new Produto("Leite 1L", "Leite integral", "Laticínios", new BigDecimal("5.29"), 120),
				new Produto("Queijo Mussarela 500g", "Fatiado", "Laticínios", new BigDecimal("24.90"), 25),
				new Produto("Banana Prata (kg)", "Fruta fresca", "Hortifruti", new BigDecimal("6.99"), 60),
				new Produto("Tomate (kg)", "Tomate italiano", "Hortifruti", new BigDecimal("8.90"), 45),
				new Produto("Pão Francês (kg)", "Assado no dia", "Padaria", new BigDecimal("15.90"), 30),
				new Produto("Detergente 500ml", "Neutro", "Limpeza", new BigDecimal("2.79"), 100),
				new Produto("Sabão em Pó 1kg", "Lavagem de roupas", "Limpeza", new BigDecimal("13.50"), 35)));
	}

}
