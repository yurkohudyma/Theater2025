package ua.hudyma.Theater2025.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.hudyma.Theater2025.security.CustomAuthenticationEntryPoint;
import ua.hudyma.Theater2025.security.CustomLogoutSuccessHandler;
import ua.hudyma.Theater2025.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String ADMIN = "ADMIN";
    public static final String MANAGER = "MANAGER";
    public static final String USER = "USER";
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                //.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                /*.csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )*/
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/auth/check",
                                "/login",
                                "/register",
                                "/login/register",
                                "/login/logout",
                                "/login/process-form",
                                "/error",
                                "/css/**",
                                "/img/**",
                                "/js/**"
                        ).permitAll()
                        .requestMatchers("/admin").hasAnyRole(ADMIN, MANAGER)
                        .requestMatchers("/user/buy/**").hasAnyRole(ADMIN, MANAGER, USER)
                        .requestMatchers("/buy").hasAnyRole(ADMIN, MANAGER, USER)
                        .requestMatchers("/user").permitAll()
                        .requestMatchers("/liqpay-callback").permitAll()
                        .requestMatchers("/access/buy").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole(ADMIN, MANAGER)
                        .requestMatchers("/payment_status/**").permitAll()
                        .anyRequest().authenticated()
                )
                /*.exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))*/
                        /*.accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // замінюємо 403 на 401
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
                        })*/
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                /*.formLogin((form -> form
                        .loginPage( "/login")
                        .usernameParameter("email")// шлях до HTML-сторінки
                        //.defaultSuccessUrl("/admin", true) // або куди редіректити після успіху
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                ))*/
                .logout(logout -> logout
                        .logoutUrl("/login/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                )
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }



}
