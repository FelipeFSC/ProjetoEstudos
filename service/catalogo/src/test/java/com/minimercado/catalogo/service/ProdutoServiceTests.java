package com.minimercado.catalogo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.minimercado.catalogo.model.Produto;
import com.minimercado.catalogo.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ProdutoServiceTests {

	private final ProdutoRepository produtoRepository = mock(ProdutoRepository.class);
	private final ProdutoService produtoService = new ProdutoService(produtoRepository);
	private Produto arroz;

	@BeforeEach
	void setUp() {
		arroz = new Produto("Arroz 5kg", null, "Mercearia", new BigDecimal("27.90"), 10);
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(arroz));
		when(produtoRepository.save(any())).thenAnswer((invocation) -> invocation.getArgument(0));
	}

	@Test
	void baixaEReporEstoque() {
		assertThat(produtoService.baixarEstoque(1L, 3).getEstoque()).isEqualTo(7);
		assertThat(produtoService.reporEstoque(1L, 2).getEstoque()).isEqualTo(9);
	}

	@Test
	void recusaBaixaMaiorQueOEstoque() {
		assertThatThrownBy(() -> produtoService.baixarEstoque(1L, 11))
			.isInstanceOfSatisfying(ResponseStatusException.class,
					(ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT));
		assertThat(arroz.getEstoque()).isEqualTo(10);
		verify(produtoRepository, never()).save(any());
	}

	@Test
	void informaProdutoInexistenteComoNaoEncontrado() {
		when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> produtoService.buscar(99L))
			.isInstanceOfSatisfying(ResponseStatusException.class,
					(ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
	}

}
