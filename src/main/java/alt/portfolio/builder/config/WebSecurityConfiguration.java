package alt.portfolio.builder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;


import alt.portfolio.builder.services.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    private final UserService userService;

    public WebSecurityConfiguration(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    		http.authorizeHttpRequests(auth -> auth
    			.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/"),
    			PathPatternRequestMatcher.withDefaults().matcher("/users"),
    			PathPatternRequestMatcher.withDefaults().matcher("/register"),
    			PathPatternRequestMatcher.withDefaults().matcher("/users/createUser/**"),
    			PathPatternRequestMatcher.withDefaults().matcher("/css"),
    			PathPatternRequestMatcher.withDefaults().matcher("/images"))			.permitAll().anyRequest().authenticated()).csrf(AbstractHttpConfigurer::disable)
    				.formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/users", true).permitAll());
    		return http.build();
    	}
    

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userService);
        auth.setPasswordEncoder(getPasswordEncoder());
        return auth;
    }
}