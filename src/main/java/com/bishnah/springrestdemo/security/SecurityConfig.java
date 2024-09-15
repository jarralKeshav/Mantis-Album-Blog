package com.bishnah.springrestdemo.security;

import com.bishnah.springrestdemo.service.AccountService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccountService accountService;
    private RSAKey rsaKey;

    public SecurityConfig(AccountService accountService) {
        this.accountService = accountService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        rsaKey = Jwks.generateRsaKey(); // Ensure this method generates a valid RSA key
        JWKSet jwkSet = new JWKSet(rsaKey);
        return ((jwkSelector, securityContext) -> jwkSelector.select(jwkSet));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(accountService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize               
                .requestMatchers("/resources/static/uploads/**").permitAll()
                .requestMatchers("/db-console/**").permitAll()
                .requestMatchers("/api/v1").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/download-photo").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/download-thumbnail").permitAll()
                .requestMatchers("/api/v1/albums/photos").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}/upload-photos").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/update").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/delete").permitAll()
                .requestMatchers("/api/v1/albums/add").permitAll()
                .requestMatchers("/api/v1/albums").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}/delete").permitAll()
                .requestMatchers("/api/v1/albums/{album_id}/update").permitAll()
                .requestMatchers("/api/v1/auth/users/add").permitAll()
                .requestMatchers("/api/v1/auth/profile").authenticated()
                .requestMatchers("/api/v1/auth/profile/update-password").authenticated()
                .requestMatchers("/api/v1/auth/profile/delete").authenticated().requestMatchers("/api/v1/auth/token").permitAll().requestMatchers("/api/v1/auth/users").hasAuthority("SCOPE_ADMIN").requestMatchers("/api/v1/auth/users/{user_id}/update-authorities").hasAuthority("SCOPE_ADMIN")).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }
}
