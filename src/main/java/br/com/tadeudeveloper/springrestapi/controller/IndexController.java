package br.com.tadeudeveloper.springrestapi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tadeudeveloper.springrestapi.model.Usuario;
import br.com.tadeudeveloper.springrestapi.repository.UsuarioRepository;

//@CrossOrigin(origins = "*")
@RestController /*Arquitetura REST*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	/*Serviço RESTful*/
	@GetMapping(value = "/inicio", produces = "application/json")
	public ResponseEntity init(@RequestParam (value = "nome", required = true, defaultValue = "Nome não informado") String nome,
			@RequestParam(value = "salario", required = true, defaultValue = "0") Long salario) {
		
		System.out.println("Parâmetro recebido: " + nome + " : " + salario);
		
		return new ResponseEntity("Olá Usuário REST Spring Boot. Nome: " + nome
				 + " - Salário: " + salario, HttpStatus.OK);
	}
	
	@GetMapping(value = "/teste", produces = "application/json")
	public ResponseEntity<Usuario> init2() {
		
		Usuario usuario = new Usuario();
		usuario.setId(50L);
		usuario.setLogin("tadeupalermoti@gmail.com");
		usuario.setNome("Tadeu Espíndola Palermo");
		usuario.setSenha("123456");
		
		Usuario usuario2 = new Usuario();
		usuario2.setId(8L);
		usuario2.setLogin("joao@gmail.com");
		usuario2.setNome("João");
		usuario2.setSenha("123");
		
		List<Usuario> usuarios = new ArrayList<>();
		usuarios.add(usuario);
		usuarios.add(usuario2);
		
		//return ResponseEntity.ok(usuario);
		return new ResponseEntity(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value = "v1/{id}", produces = "application/json")
	@Cacheable("cacheuser")
	public ResponseEntity<Usuario> buscarPorIdV1(@PathVariable(value = "id") Long id) {
		
		Usuario usuario = usuarioRepository.findById(id).get();	
		System.out.println("Buscar por id V1");
		return ResponseEntity.ok(usuario);
	}
	
	@GetMapping(value = "v2/{id}", produces = "application/json")
	public ResponseEntity<Usuario> buscarPorIdV2(@PathVariable(value = "id") Long id) {
		
		Usuario usuario = usuarioRepository.findById(id).get();	
		System.out.println("Buscar por id V2");
		return ResponseEntity.ok(usuario);
	}
	
	// EXEMPLO FICTÍCIO
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json") //application/pdf
	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id,
			@PathVariable(value = "venda") Long venda) {
		
		Usuario usuario = usuarioRepository.findById(id).get();	
		/*ROTINA DE CRIAÇÃO DO RELATÓRIO PDF*/	
		
		/*o retorno seria um relatório em PDF*/
		return ResponseEntity.ok(usuario);
	}	
	
	@GetMapping(value = "/", produces = "application/json", headers = "X-API-Version=v1")
	@Cacheable("cacheusuarios")
	public ResponseEntity<List<Usuario>> buscarTodosV1() throws InterruptedException {
		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();		
		System.out.println("Buscar Todos V1");
		Thread.sleep(6000);
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json", headers = "X-API-Version=v2")
	public ResponseEntity<List<Usuario>> buscarTodosV2() {
		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();		
		System.out.println("Buscar Todos V2");
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		// Caso o telefone não seja salvo automaticamente
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);		
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		/*outras rotinas antes de salvar (atualizar) podem ser feitas*/
		
		// Caso o telefone não seja salvo automaticamente
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemp = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if (!userTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);		
	}
	
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> atualizarSetId(@PathVariable("id") Long id, @RequestBody Usuario usuario) {
		
		/*outras rotinas antes de salvar (atualizar) podem ser feitas*/
		usuario.setId(id);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);		
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return  "ok";		
	}
	
	@DeleteMapping(value = "/{id}/venda", produces = "application/text")
	public String deleteVenda(@PathVariable("id") Long id) {
		
		// seria venda - iria deletar todas as vendas do usuário
		usuarioRepository.deleteById(id);
		
		// retornaria venda deletada
		return  "ok - venda";		
	}
	
	@PutMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity updateVenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		
		/*outras rotinas antes de salvar (atualizar) podem ser feitas*/
		
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity("Venda atualizada", HttpStatus.OK);		
	}	
	
	// EXEMPLO FICTÍCIO
	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity cadastrarVenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		
		/*Aqui seria o processo de venda*/
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		/*Retornaria a venda*/
		return new ResponseEntity("id user: " + iduser + " - idvenda: " + idvenda, HttpStatus.OK);		
	}
	
	
}
