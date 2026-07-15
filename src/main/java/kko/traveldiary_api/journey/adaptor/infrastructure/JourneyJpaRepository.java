package kko.traveldiary_api.journey.adaptor.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JourneyJpaRepository extends JpaRepository<JourneyEntity, Long> {
    List<JourneyEntity> findByMemberId(Long memberId);

    @Query("SELECT j FROM JourneyEntity j LEFT JOIN FETCH j.cityVisits WHERE j.id = :journeyId")
    Optional<JourneyEntity> findByIdFetchWithCityVisit(@Param("journeyId") Long journeyId);
}
