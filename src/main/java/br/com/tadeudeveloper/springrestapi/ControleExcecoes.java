package br.com.tadeudeveloper.springrestapi;

import java.sql.SQLException;
import java.util.List;

import org.postgresql.util.PSQLException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {
	
	// Tratamento geral (maioria dos erros)
	@Override
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class, MaxUploadSizeExceededException.class})
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append(ex.getMessage());
		
		if (ex instanceof MethodArgumentNotValidException) {
			msgBuilder = new StringBuilder();
			List<ObjectError> errors = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
			for (ObjectError error : errors) {
				msgBuilder.append(error.getDefaultMessage()).append("\n");
			}
		}
		
		ObjetoError objetoError = new ObjetoError();
		objetoError.setError(msgBuilder.toString());
		objetoError.setCode(status.value() + " ==> " + status.getReasonPhrase());
		
		return new ResponseEntity<>(objetoError, headers, status);
	}	
	
	

}
