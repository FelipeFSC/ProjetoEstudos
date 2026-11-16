package com.minimercado.catalogo.controller;

import com.minimercado.catalogo.model.Produto;
import com.minimercado.catalogo.service.ProdutoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Libera a tela (web/index.html) para chamar a API direto do navegador.
@CrossOrigin
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping
	public List<Produto> listar(@RequestParam(required = false) String categoria) {
		return produtoService.listar(categoria);
	}

	@GetMapping("/{id}")
	public Produto buscar(@PathVariable Long id) {
		return produtoService.buscar(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Produto criar(@Valid @RequestBody ProdutoRequest request) {
		return produtoService.criar(request.toProduto());
	}

	@PutMapping("/{id}")
	public Produto atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
		return produtoService.atualizar(id, request.toProduto());
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		produtoService.remover(id);
	}

	@PostMapping("/{id}/baixa-estoque")
	public Produto baixarEstoque(@PathVariable Long id, @Valid @RequestBody QuantidadeRequest request) {
		return produtoService.baixarEstoque(id, request.quantidade());
	}

	@PostMapping("/{id}/repor-estoque")
	public Produto reporEstoque(@PathVariable Long id, @Valid @RequestBody QuantidadeRequest request) {
		return produtoService.reporEstoque(id, request.quantidade());
	}

	public record ProdutoRequest(
			@NotBlank String nome,
			String descricao,
			String categoria,
			@NotNull @DecimalMin("0.01") BigDecimal preco,
			@NotNull @PositiveOrZero Integer estoque) {

		Produto toProduto() {
			return new Produto(nome, descricao, categoria, preco, estoque);
		}
	}

	public record QuantidadeRequest(@NotNull @Positive Integer quantidade) {
	}

}
