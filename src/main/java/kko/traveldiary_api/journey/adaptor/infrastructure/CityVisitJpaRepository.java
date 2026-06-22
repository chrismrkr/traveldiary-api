package kko.traveldiary_api.journey.adaptor.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CityVisitJpaRepository extends JpaRepository<CityVisitEntity, Long> {

    @Query("SELECT cv FROM CityVisitEntity cv JOIN FETCH cv.journey WHERE cv.id = :cityVisitId")
    Optional<CityVisitEntity> findByIdFetchWithJourney(@Param("cityVisitId") Long cityVisitId);
}
