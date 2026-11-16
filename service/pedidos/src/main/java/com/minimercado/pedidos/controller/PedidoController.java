package com.minimercado.pedidos.controller;

import com.minimercado.pedidos.model.Pedido;
import com.minimercado.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Libera a tela (web/index.html) para chamar a API direto do navegador.
@CrossOrigin
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@GetMapping
	public List<Pedido> listar() {
		return pedidoService.listar();
	}

	@GetMapping("/{id}")
	public Pedido buscar(@PathVariable Long id) {
		return pedidoService.buscar(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Pedido criar(@Valid @RequestBody PedidoRequest request) {
		List<PedidoService.Item> itens = request.itens()
			.stream()
			.map((item) -> new PedidoService.Item(item.produtoId(), item.quantidade()))
			.toList();
		return pedidoService.criar(request.cliente(), itens);
	}

	@PatchMapping("/{id}/cancelar")
	public Pedido cancelar(@PathVariable Long id) {
		return pedidoService.cancelar(id);
	}

	public record PedidoRequest(@NotBlank String cliente, @NotEmpty List<@Valid ItemRequest> itens) {
	}

	public record ItemRequest(@NotNull Long produtoId, @NotNull @Positive Integer quantidade) {
	}

}
