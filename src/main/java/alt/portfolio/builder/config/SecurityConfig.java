package alt.portfolio.builder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import alt.portfolio.builder.services.DbUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				// Routes publiques
				.requestMatchers("/", "/login", "/login/**", "/error").permitAll()
				.requestMatchers("/users/register", "/users/register/**").permitAll()
				.requestMatchers("/css/**", "/js/**", "/img/**", "/styles.css").permitAll()
				.requestMatchers("/p/**", "/cv/**").permitAll()
				.requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/uploads/**", "/p/**",
						"/cv/**")
				.permitAll()
				// Routes ADMIN uniquement
				.requestMatchers("/users").hasRole("ADMIN").requestMatchers("/users/*/delete").hasRole("ADMIN")
				// Toutes les autres routes necessitent une authentification
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").usernameParameter("username")
						.passwordParameter("password").defaultSuccessUrl("/dashboard", true)
						.failureUrl("/login?error=true").permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
				// Page d'erreur pour acces refuse
				.exceptionHandling(ex -> ex.accessDeniedPage("/dashboard?accessDenied=true"));

		return http.build();
	}

	@Primary
	@Bean
	UserDetailsService getUserDetailsService() {
		return new DbUserService();
	}

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider(UserDetailsService userService) {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService);
		auth.setPasswordEncoder(getPasswordEncoder());
		return auth;
	}
}