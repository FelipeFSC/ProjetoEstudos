package com.minimercado.pedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Pedido pedido;

	// O produto fica no banco do catálogo: aqui guardamos só o id, sem chave estrangeira entre bancos.
	@Column(nullable = false)
	private Long produtoId;

	// Nome e preço são copiados no momento da compra; se o catálogo mudar depois, o pedido não muda.
	@Column(nullable = false)
	private String nomeProduto;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal precoUnitario;

	@Column(nullable = false)
	private int quantidade;

	protected ItemPedido() {
	}

	public ItemPedido(Long produtoId, String nomeProduto, BigDecimal precoUnitario, int quantidade) {
		this.produtoId = produtoId;
		this.nomeProduto = nomeProduto;
		this.precoUnitario = precoUnitario;
		this.quantidade = quantidade;
	}

	public BigDecimal getSubtotal() {
		return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
	}

	void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Long getId() {
		return id;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public String getNomeProduto() {
		return nomeProduto;
	}

	public BigDecimal getPrecoUnitario() {
		return precoUnitario;
	}

	public int getQuantidade() {
		return quantidade;
	}

}
