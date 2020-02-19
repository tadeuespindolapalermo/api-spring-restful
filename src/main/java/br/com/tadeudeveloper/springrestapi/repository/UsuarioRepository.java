package br.com.tadeudeveloper.springrestapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.tadeudeveloper.springrestapi.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	
	@Query("SELECT u FROM Usuario u WHERE u.login = ?1")
	Usuario findUserByLogin(String login);	
	
	List<Usuario> findByNomeContainingIgnoreCase(String nome);
	
	@Query(value = "SELECT constraint_name from information_schema.constraint_column_usage where table_name = 'usuarios_role' and column_name = 'role_id' and constraint_name <> 'unique_role_user'", nativeQuery = true)
	String consultarConstraintRole();		
	
	@Transactional
	@Modifying
	@Query(value = "insert into usuarios_role (usuario_id, role_id) values (?1, (select id from role where nome_role = 'ROLE_USER'));", nativeQuery = true)
	void inserirAcessoRolePadrao(Long idUsuario);
	
}
