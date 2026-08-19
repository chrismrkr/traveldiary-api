package kko.traveldiary_api.shared.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 생성(POST) 요청의 중복 실행을 막는 멱등키 필터.
 * 클라이언트는 {@code Idempotency-Key} 헤더(작업당 UUID)를 반드시 보내야 한다.
 *
 * <p>핸들러 실행 전에 키를 INSERT 하고, 유니크 제약 위반이면 이미 접수된 요청으로 보고 409 를 반환한다.
 * 중복 판정은 이 INSERT 하나로 끝나므로 요청 처리 후에 기록할 것이 없다.
 *
 * <p>응답 재생(replay)은 하지 않는다. 즉 최초 요청의 응답이 네트워크에서 유실되면 클라이언트는
 * 409 를 받게 되고, 생성 여부는 조회로 직접 확인해야 한다.
 */
public class IdempotencyKeyFilter extends OncePerRequestFilter {
    public static final String HEADER = "Idempotency-Key";

    private final IdempotencyStore store;
    private final ObjectMapper objectMapper;

    public IdempotencyKeyFilter(IdempotencyStore store, ObjectMapper objectMapper) {
        this.store = store;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String key = request.getHeader(HEADER);
        if (key == null || key.isBlank()) {
            writeJson(response, HttpStatus.BAD_REQUEST, "IDEMPOTENCY_KEY_REQUIRED",
                    "Idempotency-Key header is required.");
            return;
        }

        if (!store.reserve(key, resolveMemberId())) {
            writeJson(response, HttpStatus.CONFLICT, "DUPLICATE_IDEMPOTENCY_KEY",
                    "A request with the same Idempotency-Key has already been received.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Long resolveMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            try {
                return Long.valueOf(jwt.getSubject());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, String code, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("status", code, "message", message)));
    }
}
