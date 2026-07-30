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
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * 생성(POST) 요청의 중복 실행을 막는 멱등키 필터.
 * 클라이언트는 {@code Idempotency-Key} 헤더(작업당 UUID)를 반드시 보내야 한다.
 *
 * <p>reserve-first 전략: 핸들러 실행 전에 키를 선점하고, 유니크 제약으로 동시 재시도를 차단한다.
 * 최초 요청만 핸들러를 타고 응답을 저장하며, 재요청은 저장된 응답을 그대로 재생(replay)한다.
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
            // 이미 존재하는 키 → 완료면 재생, 처리 중이면 409
            Optional<IdempotencyRecord> existing = store.find(key);
            if (existing.isPresent() && existing.get().isCompleted()) {
                replay(response, existing.get());
            } else {
                writeJson(response, HttpStatus.CONFLICT, "IDEMPOTENT_REQUEST_IN_PROGRESS",
                        "A request with the same Idempotency-Key is already in progress.");
            }
            return;
        }

        // 예약 성공 → 최초 요청. 응답을 캡처한다.
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        boolean stored = false;
        try {
            filterChain.doFilter(request, wrapper);
            int status = wrapper.getStatus();
            if (status >= 200 && status < 300) {
                String body = new String(wrapper.getContentAsByteArray(), resolveCharset(wrapper));
                store.complete(key, status, body);
                stored = true;
            }
        } finally {
            if (!stored) {
                // 실패/예외 → 예약 취소하여 정상적인 재시도를 허용한다.
                store.release(key);
            }
            wrapper.copyBodyToResponse();
        }
    }

    private void replay(HttpServletResponse response, IdempotencyRecord record) throws IOException {
        response.setStatus(record.responseStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        if (record.responseBody() != null) {
            response.getWriter().write(record.responseBody());
        }
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

    private Charset resolveCharset(ContentCachingResponseWrapper wrapper) {
        String encoding = wrapper.getCharacterEncoding();
        return encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, String code, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("status", code, "message", message)));
    }
}
