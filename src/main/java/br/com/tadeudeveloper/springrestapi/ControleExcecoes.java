package br.com.tadeudeveloper.springrestapi;

import java.sql.SQLException;

import org.postgresql.util.PSQLException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {
	
	// Tratamento geral (maioria dos erros)
	@Override
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {		
		
		ObjetoError objetoError = new ObjetoError();
		objetoError.setError(ex.getMessage());
		objetoError.setCode(status.value() + " ==> " + status.getReasonPhrase());
		
		return new ResponseEntity<>(objetoError, headers, status);
	}
	
	// Tratamento geral (maioria dos erros a nível do banco de dados)	
	@ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class, PSQLException.class, SQLException.class})
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {		
		
		String msg = ex.getMessage();
		
		if (ex instanceof DataIntegrityViolationException) {
			msg = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
		}
		
		if (ex instanceof ConstraintViolationException) {
			msg = ((ConstraintViolationException) ex).getCause().getCause().getMessage();
		}
		
		if (ex instanceof PSQLException) {
			msg = ((PSQLException) ex).getCause().getCause().getMessage();
		}
		
		if (ex instanceof SQLException) {
			msg = ((SQLException) ex).getCause().getCause().getMessage();
		}	
		
		ObjetoError objetoError = new ObjetoError();
		objetoError.setError(msg);
		objetoError.setCode(HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		
		return new ResponseEntity<>(objetoError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
