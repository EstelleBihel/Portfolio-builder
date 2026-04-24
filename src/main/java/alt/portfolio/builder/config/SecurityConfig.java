package alt.portfolio.builder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import alt.portfolio.builder.services.DbUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/login", "/login/**", "/error").permitAll()
						.requestMatchers("/users/register", "/users/register/**").permitAll()
						.requestMatchers("/css/**", "/js/**", "/img/**", "/styles.css").permitAll()
						.requestMatchers("/images/**", "/uploads/**").permitAll().requestMatchers("/p/**", "/cv/**")
						.permitAll().requestMatchers("/users").hasRole("ADMIN").requestMatchers("/users/*/delete")
						.hasRole("ADMIN").anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/perform_login")
						.usernameParameter("username").passwordParameter("password")
						.defaultSuccessUrl("/dashboard", true).failureUrl("/login?error=true").permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
				.exceptionHandling(ex -> ex.accessDeniedPage("/dashboard?accessDenied=true"));

		return http.build();
	}

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider(DbUserService dbUserService) {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(dbUserService);
		auth.setPasswordEncoder(getPasswordEncoder());
		return auth;
	}
}