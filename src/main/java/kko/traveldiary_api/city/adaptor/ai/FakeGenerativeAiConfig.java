package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.application.required.CityDescriptionGenerator;
import kko.traveldiary_api.city.application.required.CityImageGenerator;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityImage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

/**
 * 생성형 AI 를 호출하지 않는 Fake 구현.
 * {@code prod} 가 아닌 모든 프로파일(local/dev/test)에서 등록되어,
 * 로컬 실행/테스트 시 실제 AI 비용·호출을 피한다.
 * (특정 동작 검증이 필요한 테스트는 @MockitoBean 으로 덮어쓰면 된다.)
 */
@Configuration
@Profile("!prod")
public class FakeGenerativeAiConfig {

    @Bean
    public CityDescriptionGenerator cityDescriptionGenerator() {
        return city -> new CityDescription(
                "test-overview", "test-history", "test-funfact", "test-localtip");
    }

    @Bean
    public CityImageGenerator cityImageGenerator() {
        return description -> new CityImage(UUID.randomUUID().toString(), new byte[]{1, 2, 3});
    }
}
