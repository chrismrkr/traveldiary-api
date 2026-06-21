package kko.traveldiary_api.city.adaptor.infrastructure.db;

import kko.traveldiary_api.city.adaptor.infrastructure.db.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface CityJpaRepository extends JpaRepository<CityEntity, Long> {
    Optional<CityEntity> findByCoordinateLatitudeAndCoordinateLongitude(
            BigDecimal latitude, BigDecimal longitude
    );
    Optional<CityEntity> findByPlaceId(String placeId);



}
