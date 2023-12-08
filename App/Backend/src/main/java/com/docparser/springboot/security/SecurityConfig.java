package com.docparser.springboot.security;

import com.docparser.springboot.errorHandler.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.OutputStream;
import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    // Injection of custom OAuth2, success handler, and JWT filter services.
    private final CustomOAuth2UserService customOauth2UserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Define the CORS configuration for the application
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Define the origins which are allowed to make cross-origin requests
        configuration.addAllowedOrigin("https://dev.d3gfcwg1uu11c0.amplifyapp.com/");
        configuration.addAllowedOrigin("http://localhost:3000/");
        // Define the list of allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // Define the list of allowed HTTP headers in requests
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Apply the CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Define the security filter chain for the application
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (common in REST APIs)
                .csrf(AbstractHttpConfigurer::disable)
                // Configure the authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Permit all requests to specified paths
                        .requestMatchers("/auth/**", "/actuator/**").permitAll()
                        .requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // All other requests must be authenticated
                        .anyRequest().authenticated())
                // Configure OAuth2 login
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(
                                userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService))
                        .successHandler(customAuthenticationSuccessHandler))
                // Configure the route behaviour when the user logs out
                .logout(l -> l.logoutSuccessUrl("/").permitAll())
                /*
                 * Register JWT authentication filter before the standard
                 * UsernamePasswordAuthenticationFilter.
                 */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Configure session management to be stateless (suitable for REST APIs)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure exception handling, especially for authentication errors
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Create a custom error response.
                            ErrorResponse re = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Unauthorized",
                                    authException.getMessage());

                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            // Setting the CORS header 'Access-Control-Allow-Origin'
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                            OutputStream responseStream = response.getOutputStream();
                            ObjectMapper mapper = new ObjectMapper();

                            mapper.writeValue(responseStream, re);
                            responseStream.flush();
                        }));

        return http.build();
    }
}
