package com.minimercado.pedidos.repository;

import com.minimercado.pedidos.model.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	// O @EntityGraph já traz os itens na mesma consulta.
	@EntityGraph(attributePaths = "itens")
	List<Pedido> findAllByOrderByIdDesc();

	@EntityGraph(attributePaths = "itens")
	Optional<Pedido> findWithItensById(Long id);

}
