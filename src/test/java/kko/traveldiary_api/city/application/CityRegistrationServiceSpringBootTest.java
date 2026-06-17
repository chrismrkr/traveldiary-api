package kko.traveldiary_api.city.application;

import kko.traveldiary_api.city.adaptor.infrastructure.CityJpaRepository;
import kko.traveldiary_api.city.application.required.*;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityGenerateRequest;
import kko.traveldiary_api.city.domain.CityImage;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class CityRegistrationServiceSpringBootTest {

    @Autowired
    CityRegistrationService cityRegistrationService;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CityJpaRepository jpaRepository;

    @MockitoBean
    CityRegistrationEventPublisher eventPublisher;
    @MockitoBean
    CityDescriptionGenerator cityDescriptionGenerator;
    @MockitoBean
    CityImageGenerator cityImageGenerator;
    @MockitoBean
    CityImageStoragePort imageStorage;


    @BeforeEach
    void init() {
        willDoNothing().given(eventPublisher).publish(any());
        jpaRepository.deleteAll();
    }

    @Test
    void 동일한_register가_중복_호출되더라도_publish는_1번만_호출됨() {
        // given
        String name = "seoul";
        String placeId = "placeId-123123";
        Coordinate coordinate = new Coordinate(123.123, 890.102);

        // when
        cityRegistrationService.register(name, placeId, coordinate);
        cityRegistrationService.register(name, placeId, coordinate);
        cityRegistrationService.register(name, placeId, coordinate);

        // then
        await().atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> verify(eventPublisher, times(1)).publish(any()));
        City city = cityRepository.findByPlaceId(placeId).get();
        Assertions.assertTrue(city.getId() > 0);
    }

    @Test
    void city의_설명과_이미지를_정상적으로_저장할_수_있다() {
        // given
        String name = "seoul";
        String placeId = "placeId-123123";
        Coordinate coordinate = new Coordinate(123.123, 890.102);
        given(cityDescriptionGenerator.generate(any())).willReturn(new CityDescription("summary"));
        given(cityImageGenerator.generate(any())).willReturn(new CityImage("id-123", "abcabc".getBytes(StandardCharsets.UTF_8)));
        willDoNothing().given(imageStorage).save(any(), any());

        cityRegistrationService.register(name, placeId, coordinate);

        // when
        cityRegistrationService.registerDetail(new CityGenerateRequest(name, placeId, coordinate));

        // then
        Assertions.assertEquals(cityRepository.findByPlaceId(placeId).get().getStatus(), City.Status.READY);
    }

    @Test
    void city의_설명과_이미지_생성_및_저장_중_에러_시_STATUS가_FAILED로_저장된다() {
        // given
        String name = "seoul";
        String placeId = "placeId-123123";
        Coordinate coordinate = new Coordinate(123.123, 890.102);
        given(cityDescriptionGenerator.generate(any())).willReturn(new CityDescription("summary"));
        given(cityImageGenerator.generate(any())).willReturn(new CityImage("id-123", "abcabc".getBytes(StandardCharsets.UTF_8)));
        willThrow(new IllegalArgumentException("PERSIST FAILED")).given(imageStorage).save(any(), any());

        cityRegistrationService.register(name, placeId, coordinate);

        // when
        cityRegistrationService.registerDetail(new CityGenerateRequest(name, placeId, coordinate));

        // then
        Assertions.assertEquals(cityRepository.findByPlaceId(placeId).get().getStatus(), City.Status.FAILED);
    }


}