package kko.traveldiary_api.journey.application.provided;

import kko.traveldiary_api.journey.domain.Journey;

import java.util.List;
import java.util.Optional;

public interface JourneyFinder {
    List<Journey> findMyJourneys(Long memberId);
    List<Journey> findMyJourneysWithCityVisit(Long memberId);
    Journey findJourney(Long memberId, Long journeyId);
}
