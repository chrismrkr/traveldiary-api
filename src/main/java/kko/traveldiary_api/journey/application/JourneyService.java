package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.application.provided.JourneyFinder;
import kko.traveldiary_api.journey.application.provided.JourneyModifier;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import kko.traveldiary_api.journey.dto.JourneyRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyFinder, JourneyModifier {
    private final JourneyRepository repository;

    @Override
    public List<Journey> findMyJourneys(Long memberId) {
        return repository.findByMemberId(memberId);
    }

    @Override
    public Journey find(Long journeyId) {
        return repository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid JourneyId: Not Found"));
    }

    @Override
    public Journey register(JourneyRegisterDto registerDto) {
        Journey journey = Journey.builder()
                .memberId(registerDto.getMemberId())
                .startDate(registerDto.getStartDate())
                .endDate(registerDto.getEndDate())
                .name(registerDto.getName())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .isActive(true)
                .visibility(Visibility.valueOf(registerDto.getVisibility()))
                .build();
        return repository.save(journey);
    }

    @Override
    @Transactional
    public Journey modifyDate(Long journeyId, LocalDate startDate, LocalDate endDate) {
        Journey journey = repository.findById(journeyId).orElseThrow(() -> new IllegalArgumentException("Invalid JourneyId: Not Found"));
        journey.changeStartDate(startDate);
        journey.changeEndDate(endDate);
        return repository.save(journey);
    }

    @Override
    @Transactional
    public void adjustVisibility(Long journeyId, Visibility visibility) {
        Journey journey = repository.findById(journeyId).orElseThrow(() -> new IllegalArgumentException("Invalid JourneyId: Not Found"));
        journey.modifyVisibility(visibility);
        repository.save(journey);
    }
}
