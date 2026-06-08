package kko.traveldiary_api.city.adaptor.infrastructure;

import kko.traveldiary_api.city.adaptor.CityEntity;
import kko.traveldiary_api.city.application.required.CityRepository;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.shared.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CityDatabaseRepository implements CityRepository {
    private final CityJpaRepository jpaRepository;

    @Override
    public Optional<City> findByCoordinate(Coordinate coordinate) {
        return jpaRepository.findByCoordinateLatitudeAndCoordinateLongitude(
                coordinate.getLatitude(), coordinate.getLongitude()
        ).map(CityEntity::toDomain);
    }

    @Override
    public Optional<City> findByPlaceId(String placeId) {
        return jpaRepository.findByPlaceId(placeId).map(CityEntity::toDomain);
    }

    @Override
    public City save(City city) {
        CityEntity entity = CityEntity.from(city);
        entity = jpaRepository.save(entity);
        return entity.toDomain();
    }
}
