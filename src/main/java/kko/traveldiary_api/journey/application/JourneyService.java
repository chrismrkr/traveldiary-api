package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.provided.JourneyFinder;
import kko.traveldiary_api.journey.application.provided.JourneyManager;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyRegisterReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyFinder, JourneyManager {
    private final JourneyRepository repository;

    @Override
    public List<Journey> findMyJourneys(Long memberId) {
        return repository.findByMemberId(memberId);
    }

    @Override
    public Journey findJourney(Long journeyId) {
        return repository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid JourneyId: Not Found"));
    }

    @Override
    public Journey register(JourneyRegisterReqDto registerDto) {
        Journey journey = Journey.builder()
                .memberId(registerDto.memberId())
                .startDate(registerDto.startDate())
                .endDate(registerDto.endDate())
                .name(registerDto.name())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .isActive(true)
                .visibility(Visibility.valueOf(registerDto.visibility()))
                .build();
        return repository.save(journey);
    }

    @Override
    public Journey modify(JourneyPatchReqDto patchReqDto) {
        Journey journey = repository.findByIdWithCityVisit(patchReqDto.journeyId()).orElseThrow(() ->
                new IllegalArgumentException("Invalid JourneyId: Not Found"));

        if(patchReqDto.startDate() != null && patchReqDto.endDate() != null) {
            journey.changeStartDate(patchReqDto.startDate(), journey.getCityVisitList());
            journey.changeEndDate(patchReqDto.endDate(), journey.getCityVisitList());
        }
        if(patchReqDto.name() != null) {
            journey.changeName(patchReqDto.name());
        }
        if(patchReqDto.visibility() != null) {
            journey.modifyVisibility(Visibility.valueOf(patchReqDto.visibility()));
        }
        return repository.save(journey);
    }

    @Override
    public void delete(Long journeyId) {
        repository.deleteJourney(journeyId);
    }
}
