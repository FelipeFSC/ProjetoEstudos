package com.minimercado.catalogo.repository;

import com.minimercado.catalogo.model.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	List<Produto> findAllByOrderByNome();

	List<Produto> findByCategoriaIgnoreCaseOrderByNome(String categoria);

}
