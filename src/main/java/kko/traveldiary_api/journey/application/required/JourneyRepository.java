package kko.traveldiary_api.journey.application.required;

import kko.traveldiary_api.journey.domain.Journey;

import java.util.List;
import java.util.Optional;

public interface JourneyRepository {
    List<Journey> findByMemberId(Long memberId);
    Optional<Journey> findById(Long journeyId);
    Journey save(Journey journey);

}
