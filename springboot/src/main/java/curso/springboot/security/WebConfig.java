package curso.springboot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
 @Override
 public void addResourceHandlers(ResourceHandlerRegistry registry) {
registry.addResourceHandler("/webjars/**", "/resources/**", "/static/**", "/img/**", "/css/**", "/js/**", "/matererialize/**",
				"classpath:/static/", "classpath:/resources/")
		.addResourceLocations("/webjars/", "/resources/",
						"classpath:/static/**", "classpath:/static/img/**", "classpath:/static/",
						"classpath:/resources/", "classpath:/static/css/", "classpath:/static/materialize/", "classpath:/static/js/", "/resources/**",
						"/WEB-INF/classes/static/**");
		
 }
}
