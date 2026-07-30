package kko.traveldiary_api.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import kko.traveldiary_api.shared.idempotency.IdempotencyKeyFilter;
import kko.traveldiary_api.shared.idempotency.IdempotencyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // FilterSecurityInterceptor는 AuthorizationFilter로 명칭이 변경됨(Security 6.xx)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationEntryPoint authenticationEntryPoint,
                                           IdempotencyStore idempotencyStore, ObjectMapper objectMapper) throws Exception {
        http
                // JWT 기반이라 CSRF 불필요 (세션 안 씀)
                .csrf(AbstractHttpConfigurer::disable)
                // 세션을 안 만들도록 (stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 엔드포인트별 인증 정책
                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI 문서는 인증 없이 접근 허용
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                // access token 검증 (공개키 기반 JwtDecoder 사용)
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults())
                                .authenticationEntryPoint(authenticationEntryPoint))

        // 인증 처리 실패 중 에러 (401)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authenticationEntryPoint))

                // 인증 필터 뒤에 멱등키 필터 배치 (memberId 를 SecurityContext 에서 읽기 위함)
                .addFilterAfter(new IdempotencyKeyFilter(idempotencyStore, objectMapper),
                        BearerTokenAuthenticationFilter.class);

        // 권한 처리 실패 중 에러 (403)
//                .exceptionHandling(ex ->
//                        ex.accessDeniedHandler());
        return http.build();
    }

}
