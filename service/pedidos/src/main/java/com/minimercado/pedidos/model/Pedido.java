package com.minimercado.pedidos.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String cliente;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPedido status;

	@Column(nullable = false)
	private Instant criadoEm;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemPedido> itens = new ArrayList<>();

	protected Pedido() {
	}

	public Pedido(String cliente) {
		this.cliente = cliente;
		this.status = StatusPedido.CRIADO;
		this.criadoEm = Instant.now();
	}

	public void adicionarItem(ItemPedido item) {
		item.setPedido(this);
		itens.add(item);
	}

	public BigDecimal getTotal() {
		return itens.stream().map(ItemPedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public Long getId() {
		return id;
	}

	public String getCliente() {
		return cliente;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public Instant getCriadoEm() {
		return criadoEm;
	}

	public List<ItemPedido> getItens() {
		return itens;
	}

}
