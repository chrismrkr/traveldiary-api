package kko.traveldiary_api.city.application;

import kko.traveldiary_api.city.adaptor.infrastructure.CityJpaRepository;
import kko.traveldiary_api.city.application.provided.CityFinder;
import kko.traveldiary_api.city.application.required.CityDescriptionGenerator;
import kko.traveldiary_api.city.application.required.CityRepository;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityNotReadyException;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * CityService 통합 테스트.
 *
 * 실제 ApplicationEventPublisher 와 이벤트 리스너 빈을 그대로 띄워, 등록 시
 * Publisher → Listener → CityDetailRegistration 포트까지 이벤트 체인이 동작하는지 검증한다.
 * 상세 생성(AI 호출) 어댑터인 {@link CityDescriptionGenerator} 는 Mock으로 대체한다.
 */
@SpringBootTest
class CityServiceTest {

    @Autowired
    private CityFinder cityFinder;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityJpaRepository cityJpaRepository;

    @MockitoBean
    private CityDescriptionGenerator cityDescriptionGenerator;


    @AfterEach
    @BeforeEach
    void tearDown() {
        cityJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("좌표에 해당하는 City가 이미 존재하면 그대로 반환하고, 등록/이벤트 흐름은 일어나지 않는다")
    void find_whenCityExists_returnsItWithoutTriggeringRegistration() {
        Coordinate coordinate = new Coordinate(37.5665, 126.9780);
        cityRepository.save(City.builder()
                .name("Seoul")
                .placeId("place-seoul")
                .cityDescription(new CityDescription("대한민국의 수도", "", "", ""))
                .cityImageId("img-seoul")
                .coordinate(coordinate)
                .status(City.Status.READY)
                .build());

        City found = cityFinder.findByCoordinate(coordinate);

        assertThat(found.getName()).isEqualTo("Seoul");
        assertThat(found.getStatus()).isEqualTo(City.Status.READY);
        // 이미 존재하므로 register() → 이벤트 발행 → 리스너 → 상세 생성 흐름이 트리거되지 않아야 한다.
        verify(cityDescriptionGenerator, never()).generate(any());
    }

    @Test
    @DisplayName("좌표에 해당하는 City가 없으면 CityNotReadyException을 던지고, Publisher→Listener를 거쳐 상세 생성이 호출된다")
    void find_whenCityAbsent_triggersRegistrationAndEventChain() {
        String name = "busan";
        String placeId = "place-busan";
        Coordinate coordinate = new Coordinate(35.1796, 129.0756); // 부산
        given(cityDescriptionGenerator.generate(any())).willAnswer(invocation -> {
            City city = invocation.getArgument(0);
            city.saveDetails(new CityDescription("부산에 대한 설명", "", "", ""), "img-busan");
            city.setStatus(City.Status.READY);
            return city;
        });

        // 좌표 조회 실패 → 비동기 등록을 트리거하고 "아직 준비되지 않음"을 알린다.
        assertThatThrownBy(() -> cityFinder.findOrRegister(name, placeId, coordinate))
                .isInstanceOf(CityNotReadyException.class);

        // register() 가 PENDING City 를 먼저 저장했어야 한다.
        Optional<City> city = cityRepository.findByCoordinate(coordinate);
        assertThat(city).isPresent();
        Assertions.assertTrue(city.get().getId() > 0);
        Assertions.assertEquals(city.get().getCityDescription().overview(), "부산에 대한 설명");

        // 이벤트가 발행되고(Publisher) 리스너가 이를 수신하여(Listener)
        // CityDetailRegistration 포트가 호출되어 상세 생성기까지 도달했는지 검증한다.
        await().atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> verify(cityDescriptionGenerator, times(1)).generate(any()));
    }
}
