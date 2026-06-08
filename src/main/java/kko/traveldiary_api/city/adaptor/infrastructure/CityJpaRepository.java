package kko.traveldiary_api.city.adaptor.infrastructure;

import kko.traveldiary_api.city.adaptor.CityEntity;
import kko.traveldiary_api.city.adaptor.CoordinateEmbeddable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

public interface CityJpaRepository extends JpaRepository<CityEntity, Long> {
    Optional<CityEntity> findByCoordinateLatitudeAndCoordinateLongitude(
            BigDecimal latitude, BigDecimal longitude
    );
    Optional<CityEntity> findByPlaceId(String placeId);



}
