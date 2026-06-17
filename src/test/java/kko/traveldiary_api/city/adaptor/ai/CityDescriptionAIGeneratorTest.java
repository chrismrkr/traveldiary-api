package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.shared.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CityDescriptionAIGeneratorTest {
    @Autowired CityDescriptionAIGenerator cityDescriptionAIGenerator;
    @Value("${spring.ai.anthropic.api-key:NO_SET}")
    private String apiKey;


    @Test
    void City의_이름_위도_경도를_활용하여_도시_설명을_생성형_AI에게_요청할_수_있다() {
        // given
        log.info("apiKey: {}", apiKey);
        log.info("System Env: {}", System.getenv("ANTHROPIC_API_KEY"));
        City seoul = City.builder()
                .name("Seoul")
                .placeId("Seoul-001")
                .coordinate(new Coordinate(37.5665, 126.9780))
                .build();

        // when
        CityDescription description = cityDescriptionAIGenerator.generate(seoul);

        // then
        Assertions.assertNotNull(description.summary());
    }

    @Test
    void 이름_위도_경도_중_하나만_없어도_빈_값을_반환_받는다() {

    }

}