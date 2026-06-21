package kko.traveldiary_api.city.adaptor.infrastructure;

import kko.traveldiary_api.city.adaptor.infrastructure.db.CityDatabaseRepository;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CityDatabaseRepository.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record CityDatabaseRepositoryTest(CityDatabaseRepository cityDatabaseRepository) {
    private City sampleCity() {
        return City.builder()
                .placeId("place-123")
                .name("Seoul")
                .cityDescription(new CityDescription("대한민국의 수도", "조선의 도읍", "한강이 흐른다", "지하철을 이용하세요"))
                .cityImageId("image-1")
                .coordinate(new Coordinate(37.5665, 126.9780))
                .status(City.Status.READY)
                .build();
    }

    @Test
    @DisplayName("City를 저장하면 도메인 객체로 반환된다")
    void save() {
        City saved = cityDatabaseRepository.save(sampleCity());

        assertThat(saved).isNotNull();
        assertThat(saved.getPlaceId()).isEqualTo("place-123");
        assertThat(saved.getName()).isEqualTo("Seoul");
        assertThat(saved.getCityDescription().overview()).isEqualTo("대한민국의 수도");
        assertThat(saved.getCityImageId()).isEqualTo("image-1");
        assertThat(saved.getStatus()).isEqualTo(City.Status.READY);
        assertThat(saved.getCoordinate().getLatitude()).isEqualByComparingTo("37.566500");
        assertThat(saved.getCoordinate().getLongitude()).isEqualByComparingTo("126.978000");
    }

    @Test
    @DisplayName("placeId로 저장된 City를 조회한다")
    void findByPlaceId() {
        cityDatabaseRepository.save(sampleCity());

        Optional<City> found = cityDatabaseRepository.findByPlaceId("place-123");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Seoul");
        assertThat(found.get().getStatus()).isEqualTo(City.Status.READY);
    }

    @Test
    @DisplayName("존재하지 않는 placeId로 조회하면 비어있다")
    void findByPlaceId_notFound() {
        Optional<City> found = cityDatabaseRepository.findByPlaceId("not-exist");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("좌표로 저장된 City를 조회한다")
    void findByCoordinate() {
        cityDatabaseRepository.save(sampleCity());

        Optional<City> found = cityDatabaseRepository.findByCoordinate(new Coordinate(37.5665, 126.9780));

        assertThat(found).isPresent();
        assertThat(found.get().getPlaceId()).isEqualTo("place-123");
        assertThat(found.get().getName()).isEqualTo("Seoul");
    }

    @Test
    @DisplayName("존재하지 않는 좌표로 조회하면 비어있다")
    void findByCoordinate_notFound() {
        cityDatabaseRepository.save(sampleCity());

        Optional<City> found = cityDatabaseRepository.findByCoordinate(new Coordinate(0.0, 0.0));

        assertThat(found).isEmpty();
    }
}
