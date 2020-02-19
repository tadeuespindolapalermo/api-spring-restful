package br.com.tadeudeveloper.springrestapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.tadeudeveloper.springrestapi.model.Usuario;
import br.com.tadeudeveloper.springrestapi.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if (username == null) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}
		
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
	}

	public void inserirAcessoPadrao(Long idUsuario) {
		String constraint = usuarioRepository.consultarConstraintRole();
		
		if (constraint != null)
			jdbcTemplate.execute("alter table usuarios_role DROP CONSTRAINT " + constraint);
		
		usuarioRepository.inserirAcessoRolePadrao(idUsuario);
	}

}
