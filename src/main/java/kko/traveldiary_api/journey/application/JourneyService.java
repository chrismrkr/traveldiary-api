package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyFinder, JourneyManager {
    private final JourneyRepository repository;

    @Override
    public List<Journey> findMyJourneys(Long memberId) {
        return repository.findByMemberId(memberId);
    }

    @Override
    public Journey findJourney(Long memberId, Long journeyId) {
        return findAndValidateOwner(memberId, journeyId, false);
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
    public Journey modify(Long memberId, JourneyPatchReqDto patchReqDto) {
        Journey journey = findAndValidateOwner(memberId, patchReqDto.journeyId(), true);

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
    public void delete(Long memberId, Long journeyId) {
        findAndValidateOwner(memberId, journeyId, false);
        repository.deleteJourney(journeyId);
    }

    private Journey findAndValidateOwner(Long memberId, Long journeyId, boolean withCityVisits) {
        Journey journey = withCityVisits ? repository.findByIdWithCityVisit(journeyId).orElseThrow(() -> new JourneyNotFoundException(journeyId))
                                        : repository.findById(journeyId).orElseThrow(() -> new JourneyNotFoundException(journeyId));
        if(!journey.isOwnedBy(memberId)) {
            throw new JourneyAccessDeniedException(memberId, journeyId);
        }
        return journey;
    }

}
