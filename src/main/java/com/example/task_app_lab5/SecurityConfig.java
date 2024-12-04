package com.example.task_app_lab5;
import com.example.task_app_lab5.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Инжектим  в конфигурацию
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**","/profile/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            System.out.println("Authenticated user: " + authentication.getName());
                            System.out.println("Authorities: " + authorities);
                            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin");
                            } else {
                                response.sendRedirect("/user/tasks");
                            }
                        })
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll); //оператор для класса :: -ссылаюсь на метод
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
