package br.com.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ads.domain.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}