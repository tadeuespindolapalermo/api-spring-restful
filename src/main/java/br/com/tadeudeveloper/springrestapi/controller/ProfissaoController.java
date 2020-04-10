package br.com.tadeudeveloper.springrestapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tadeudeveloper.springrestapi.model.Profissao;
import br.com.tadeudeveloper.springrestapi.repository.ProfissaoRepository;

@RestController
@RequestMapping(value = "/profissao")
public class ProfissaoController {

	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Profissao>> listarProfissoes() {
		
		List<Profissao> profissoes = profissaoRepository.findAll();
		
		return new ResponseEntity<List<Profissao>>(profissoes, HttpStatus.OK);
		
	}

}
