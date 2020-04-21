package br.com.tadeudeveloper.springrestapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import br.com.tadeudeveloper.springrestapi.repository.TelefoneRepository;
import br.com.tadeudeveloper.springrestapi.repository.UsuarioRepository;
import br.com.tadeudeveloper.springrestapi.service.ImplementacaoUserDetailsService;
import br.com.tadeudeveloper.springrestapi.service.ServiceRelatorio;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ServiceRelatorio serviceRelatorio;

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
	public ResponseEntity<Page<Usuario>> buscarTodos() {
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		Page<Usuario> list = usuarioRepository.findAll(page);
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> buscarTodosPaginado(@PathVariable("pagina") int pagina) {
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		Page<Usuario> list = usuarioRepository.findAll(page);
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> buscarUsuarioPorNome(@PathVariable("nome") String nome) {		
		
		PageRequest pageRequest = null;
		Page<Usuario> list = null;
		
		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) { // não informou o nome
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);		
		} else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}		
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> buscarUsuarioPorNomePage(
			@PathVariable("nome") String nome, @PathVariable("page") int page) {		
		
		PageRequest pageRequest = null;
		Page<Usuario> list = null;
		
		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) { // não informou o nome
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);		
		} else {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}		
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {

		// Caso o telefone não seja salvo automaticamente
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.inserirAcessoPadrao(usuario.getId());

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		// Caso o telefone não seja salvo automaticamente
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
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
		return "ok";
	}

	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		return "ok";
	}
	
	@GetMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception {
		
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario", request.getServletContext());
		
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<>(base64Pdf, HttpStatus.OK);
	}

}
