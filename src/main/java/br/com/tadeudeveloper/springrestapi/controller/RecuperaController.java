package br.com.tadeudeveloper.springrestapi.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.tadeudeveloper.springrestapi.ObjetoError;
import br.com.tadeudeveloper.springrestapi.model.Usuario;
import br.com.tadeudeveloper.springrestapi.repository.UsuarioRepository;
import br.com.tadeudeveloper.springrestapi.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoError> recuperar(@RequestBody Usuario login) throws Exception {
		
		ObjetoError objetoError = new ObjetoError();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		
		if (user == null) {
			objetoError.setCode("404");
			objetoError.setError("Usuário não encontrado!");
		} else {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			String senhaNovaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
			
			usuarioRepository.updateSenha(senhaNovaCriptografada, user.getId());
			
			serviceEnviaEmail.enviarEmail(
					"Recuperação de senha", 
					user.getLogin(),
					"Sua nova senha é: " + senhaNova);
			
			objetoError.setCode("200");
			objetoError.setError("Aceso enviado para seu e-mail!");
		}
		
		return new ResponseEntity<>(objetoError, HttpStatus.OK);
	}

}
