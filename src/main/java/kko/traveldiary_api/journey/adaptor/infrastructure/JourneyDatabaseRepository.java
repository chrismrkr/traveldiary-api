package kko.traveldiary_api.journey.adaptor.infrastructure;


import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.Journey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JourneyDatabaseRepository implements JourneyRepository {
    @Override
    public List<Journey> findByMemberId(Long memberId) {
        return null;
    }

    @Override
    public Optional<Journey> findById(Long journeyId) {
        return Optional.empty();
    }

    @Override
    public Journey save(Journey journey) {
        return null;
    }
}
