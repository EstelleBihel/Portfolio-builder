package alt.portfolio.builder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import alt.portfolio.builder.services.DbUserService;

/**
 * Configuration de la sécurité Spring US-000 : La page d'accueil "/" est
 * publique US-001 : L'inscription "/users/register/**" est publique US-002 : La
 * connexion "/login" est publique
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Configuration de la chaîne de filtres de sécurité
	 */
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((req) -> req.requestMatchers(
				// Pages publiques - US-000
				PathPatternRequestMatcher.withDefaults().matcher("/"),
				PathPatternRequestMatcher.withDefaults().matcher("/about"),

				// Ressources statiques
				PathPatternRequestMatcher.withDefaults().matcher("/css/**"),
				PathPatternRequestMatcher.withDefaults().matcher("/js/**"),
				PathPatternRequestMatcher.withDefaults().matcher("/img/**"),
				PathPatternRequestMatcher.withDefaults().matcher("/images/**"),

				// Inscription publique - US-001
				PathPatternRequestMatcher.withDefaults().matcher("/users/register"),
				PathPatternRequestMatcher.withDefaults().matcher("/users/register/**"),

				// Portfolios/CV publics (pour plus tard - US-022)
				PathPatternRequestMatcher.withDefaults().matcher("/public/**"),
				PathPatternRequestMatcher.withDefaults().matcher("/portfolio/**"),
				PathPatternRequestMatcher.withDefaults().matcher("/cv/**")

		).permitAll().anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
		).formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/dashboard", true) // Redirection vers
																								// dashboard après login
																								// - US-002b
				.failureUrl("/login?error=true").permitAll())
				.logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/?logout=true") // Retour à l'accueil
																									// après déconnexion
						.permitAll());
		return http.build();
	}

	/**
	 * Service principal pour récupérer les détails utilisateur
	 */
	@Primary
	@Bean
	UserDetailsService getUserDetailsService() {
		return new DbUserService();
	}

	/**
	 * Bean pour encoder les mots de passe avec BCrypt
	 */
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Fournisseur d'authentification utilisant le service utilisateur et l'encodeur
	 */
	@Bean
	DaoAuthenticationProvider authenticationProvider(UserDetailsService userService) {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userService);
		auth.setPasswordEncoder(getPasswordEncoder());
		return auth;
	}
}