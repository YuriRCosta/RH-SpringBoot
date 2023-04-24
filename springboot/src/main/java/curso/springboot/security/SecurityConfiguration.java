package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService; 
	
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests( (authorize) -> authorize
        		 .requestMatchers("/").permitAll()
        		 .requestMatchers("/static/**").permitAll()
        		 .requestMatchers("/fragments/**").permitAll()
        		 .requestMatchers("/css/**").permitAll()
        		 .requestMatchers("/js/**").permitAll()
        		 .requestMatchers("/img/**").permitAll()
        		 .requestMatchers("/pesquisarpessoa").permitAll()
        		 .requestMatchers("/salvarpessoa").permitAll()
        		 .requestMatchers("/cadastropessoa").hasAnyAuthority("ROLE_ADMIN")
        		 .anyRequest().authenticated()
        		)
            .formLogin().permitAll().loginPage("/login").defaultSuccessUrl("/inicio").failureUrl("/login?error=true").and().logout().logoutSuccessUrl("/login").logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
           .requestMatchers("/static/**"); // #3
    }
    
}




