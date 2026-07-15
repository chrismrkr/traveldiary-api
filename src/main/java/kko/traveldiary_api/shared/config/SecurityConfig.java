package kko.traveldiary_api.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig {
    // FilterSecurityInterceptor는 AuthorizationFilter로 명칭이 변경됨(Security 6.xx)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT 기반이라 CSRF 불필요 (세션 안 씀)
                .csrf(AbstractHttpConfigurer::disable)
                // 세션을 안 만들도록 (stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 엔드포인트별 인증 정책
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                // access token 검증 (공개키 기반 JwtDecoder 사용)
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults()));

        // 인증 처리 실패 중 에러 (401)
//                .exceptionHandling(ex ->
//                        ex.authenticationEntryPoint(authenticationEntryPoint))

        // 권한 처리 실패 중 에러 (403)
//                .exceptionHandling(ex ->
//                        ex.accessDeniedHandler());
        return http.build();
    }

}
