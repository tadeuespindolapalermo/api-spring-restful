package br.com.tadeudeveloper.springrestapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
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
import org.springframework.web.bind.annotation.RestController;

import br.com.tadeudeveloper.springrestapi.model.Usuario;
import br.com.tadeudeveloper.springrestapi.repository.UsuarioRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;		
	
	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> buscarPorId(@PathVariable(value = "id") Long id) {		
		Usuario usuario = usuarioRepository.findById(id).get();			
		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
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
	
	@GetMapping(value = "/v1", produces = "application/json", headers = "X-API-Version=v1")
	@Cacheable("cacheusuarios")
	public ResponseEntity<List<Usuario>> buscarTodosV1() throws InterruptedException {		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();		
		System.out.println("Buscar Todos V1");
		Thread.sleep(6000);
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value = "/v2", produces = "application/json", headers = "X-API-Version=v2")
	public ResponseEntity<List<Usuario>> buscarTodosV2() {		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();		
		System.out.println("Buscar Todos V2");
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> buscarTodos() {		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();				
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> buscarUsuarioPorNome(@PathVariable("nome") String nome) {		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findByNomeContainingIgnoreCase(nome);				
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
		
		// Caso o telefone não seja salvo automaticamente
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemp = usuarioRepository.findById(usuario.getId()).get();
		
		if (!userTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);		
	}
	
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> atualizarSetId(@PathVariable("id") Long id, @RequestBody Usuario usuario) {		
		usuario.setId(id);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);		
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {		
		usuarioRepository.deleteById(id);		
		return  "ok";		
	}		
	
}
