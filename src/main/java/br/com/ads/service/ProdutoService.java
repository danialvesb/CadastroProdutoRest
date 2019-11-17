package br.com.ads.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.ads.domain.*;
import br.com.ads.repository.ProdutoRepository;
import br.com.ads.service.exception.ProdutoExistenteException;
import br.com.ads.service.exception.ProdutoNaoEncontradoException;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repository;

	public List<Produto> listar() {
		return repository.findAll();
	}
	
	public Produto buscar(Long id) {
		Produto produto = repository.findById(id).orElse(null);
		
		if (produto == null) {
			throw new ProdutoNaoEncontradoException("Produto não encontrado.");
		}
		
		return produto;
	}
	
	public Produto salvar(Produto produto) {
		verificarExistencia(produto);
		produto.setId(null);
		return repository.save(produto);
	}
	
	public Produto atualizar(Long id, Produto produto) {
		Produto produtoSalvo = buscar(id);
		BeanUtils.copyProperties(produto, produtoSalvo, "id");
		
		produtoSalvo = repository.save(produtoSalvo);
        
		return (produtoSalvo);
	}
	
	public void delete(long id) {
		
		buscar(id);

		repository.deleteById(id);

	}
	
	private void verificarExistencia(Produto produto) {
		if (produto.getId() != null && repository.findById(produto.getId()).orElse(null) != null) {
			throw new ProdutoExistenteException("Produto já cadastrado.");
		}
	}
}
