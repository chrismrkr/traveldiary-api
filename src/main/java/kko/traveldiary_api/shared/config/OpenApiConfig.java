package kko.traveldiary_api.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import kko.traveldiary_api.shared.idempotency.IdempotencyKeyFilter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI travelDiaryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TravelDiary API")
                        .description("여행 다이어리 API 문서. 인증은 JWT Bearer 토큰을 사용하며, "
                                + "생성(POST) 요청은 Idempotency-Key 헤더가 필수입니다.")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    /**
     * 모든 POST 오퍼레이션에 필수 {@code Idempotency-Key} 헤더를 자동으로 문서화한다.
     * (필터에서 강제하는 실제 계약을 문서에 그대로 반영)
     */
    @Bean
    public OperationCustomizer idempotencyKeyHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.hasMethodAnnotation(PostMapping.class)) {
                operation.addParametersItem(new Parameter()
                        .in("header")
                        .name(IdempotencyKeyFilter.HEADER)
                        .required(true)
                        .description("생성 요청 중복 방지 키. 작업당 고유한 UUID 를 보낸다. "
                                + "동일 키 재요청 시 최초 응답을 그대로 반환한다.")
                        .schema(new StringSchema()));
            }
            return operation;
        };
    }
}
