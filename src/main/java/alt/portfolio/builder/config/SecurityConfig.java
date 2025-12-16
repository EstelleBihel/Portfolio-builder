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

// Configuration de la sécurité Spring
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configuration de la chaîne de filtres de sécurité
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Désactive CSRF
            .authorizeHttpRequests(
                (req) -> req.requestMatchers(
                        PathPatternRequestMatcher.withDefaults().matcher("/"),       // Autorise la racine
                        PathPatternRequestMatcher.withDefaults().matcher("/css/**"), // Autorise le CSS
                        PathPatternRequestMatcher.withDefaults().matcher("/js/**"),  // Autorise le JS
                        PathPatternRequestMatcher.withDefaults().matcher("/img/**"), // Autorise les images
                        PathPatternRequestMatcher.withDefaults().matcher("/users/register/**") // Autorise l'inscription
                        ).permitAll()
                        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
            )
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(
                (form) -> form.loginPage("/login") // Page de login personnalisée
                                .defaultSuccessUrl("/users", true) // Redirection après login réussi
                                .permitAll() // Login accessible à tous
            );
        return http.build();
    }
    
    // Service principal pour récupérer les détails utilisateur
    @Primary
    @Bean
    UserDetailsService getUserDetailsService() {
        return new DbUserService();
    }
 
    // Bean pour encoder les mots de passe avec BCrypt
    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Fournisseur d'authentification utilisant le service utilisateur et l'encodeur
    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userService);
        auth.setPasswordEncoder(getPasswordEncoder());
        return auth;
    }
}

