package prac.demonote.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import prac.demonote.global.security.handler.JwtAccessDeniedHandler;
import prac.demonote.global.security.handler.JwtAuthenticationEntryPoint;
import prac.demonote.global.security.jwt.JwtAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(configurationSource()))

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/login").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().permitAll() // 임시
            //.anyRequest().authenticated()
        )

        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
        )
        .build();
  }

  @Bean
  public CorsConfigurationSource configurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    List<String> allowedOrigins = List.of("*");

    // configuration.setAllowedOrigins(allowedOrigins); 나중에 쓸 거
    configuration.setAllowedOriginPatterns(allowedOrigins);
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // front에서 읽게 해주려는, preflight땜에 content-type도
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(false); // 쿠키,세션 인증 안하니까 false (쿠키/세션을 자동으로 포함해서 보낼지 여부)
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
