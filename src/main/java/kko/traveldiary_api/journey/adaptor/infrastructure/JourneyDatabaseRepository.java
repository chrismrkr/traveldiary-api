package kko.traveldiary_api.journey.adaptor.infrastructure;


import kko.traveldiary_api.journey.application.required.CityVisitRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JourneyDatabaseRepository implements JourneyRepository, CityVisitRepository {
    private final JourneyJpaRepository journeyJpaRepository;
    private final CityVisitJpaRepository cityVisitJpaRepository;

    @Override
    public List<Journey> findByMemberId(Long memberId) {
        return journeyJpaRepository.findByMemberId(memberId)
                .stream().map(JourneyEntity::toDomain)
                .toList();
    }

    @Override
    public List<Journey> findByMemberIdWithCityVisit(Long memberId) {
        return journeyJpaRepository.findByMemberIdWithCityVisit(memberId)
                .stream().map(JourneyEntity::toDomainWithCityVisits).toList();
    }

    @Override
    public Optional<Journey> findById(Long journeyId) {
        return journeyJpaRepository.findById(journeyId)
                .map(JourneyEntity::toDomain);
    }

    @Override
    public Optional<Journey> findByIdWithCityVisit(Long journeyId) {
        return journeyJpaRepository.findByIdFetchWithCityVisit(journeyId)
                .map(JourneyEntity::toDomainWithCityVisits);
    }

    @Override
    public Journey save(Journey journey) {
        JourneyEntity entity = journeyJpaRepository.save(JourneyEntity.from(journey));
        return entity.toDomain();
    }

    @Override
    public void deleteJourney(Long journeyId) {
        journeyJpaRepository.deleteById(journeyId);
    }

    @Override
    public Optional<CityVisit> findCityVisitByIdWithJourney(Long cityVisitId) {
        return cityVisitJpaRepository.findByIdFetchWithJourney(cityVisitId)
                .map(CityVisitEntity::toDomain);
    }

    @Override
    public CityVisit save(CityVisit cityVisit) {
        CityVisitEntity entity = cityVisitJpaRepository.save(CityVisitEntity.from(cityVisit));
        return entity.toDomain();
    }

    @Override
    public void deleteCityVisit(Long cityVisitId) {
        cityVisitJpaRepository.deleteById(cityVisitId);
    }
}
