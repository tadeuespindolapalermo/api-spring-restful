package br.com.tadeudeveloper.springrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = { "br.com.tadeudeveloper.springrestapi.model" }) // varre pacotes de modelo
@ComponentScan(basePackages = { "br.*" }) // varre todo o projeto - injeção de dependências
@EnableJpaRepositories(basePackages = { "br.com.tadeudeveloper.springrestapi.repository" }) // habilita persistência
@EnableTransactionManagement // controle transacional (gerência de transações)
@EnableWebMvc // habilita MVC
@RestController // habilita REST (retorno de JSON)
@EnableAutoConfiguration // configuração automática do projeto
@EnableCaching // Habilita o cache
public class SpringRestApiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestApiApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

	// Libera acesso às origens específicas (mapeamento global)
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/usuario/**")
			.allowedMethods("*")
			.allowedOrigins("*");

		registry.addMapping("/profissao/**")
			.allowedMethods("*")
			.allowedOrigins("*");
		
		registry.addMapping("/recuperar/**")
			.allowedMethods("*")
			.allowedOrigins("*");
	}

}
