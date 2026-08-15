package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
import kko.traveldiary_api.journey.application.provided.JourneyFinder;
import kko.traveldiary_api.journey.application.provided.JourneyManager;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.application.required.PostQueryPort;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyRegisterReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyFinder, JourneyManager {
    private final JourneyRepository repository;
    private final PostQueryPort postQueryPort;

    @Override
    public List<Journey> findMyJourneys(Long memberId) {
        return repository.findByMemberId(memberId);
    }

    @Override
    public List<Journey> findMyJourneysWithCityVisit(Long memberId) {
        return repository.findByMemberIdWithCityVisit(memberId);
    }

    @Override
    public Journey findJourney(Long memberId, Long journeyId) {
        return findAndValidateOwner(memberId, journeyId, true);
    }

    @Override
    public Journey register(Long memberId, JourneyRegisterReqDto registerDto) {
        Journey journey = Journey.builder()
                .memberId(memberId)
                .startDate(registerDto.startDate())
                .endDate(registerDto.endDate())
                .name(registerDto.name())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .isActive(true)
                .visibility(registerDto.visibility())
                .build();
        return repository.save(journey);
    }

    @Override
    public Journey modify(Long memberId, JourneyPatchReqDto patchReqDto) {
        Journey journey = findAndValidateOwner(memberId, patchReqDto.journeyId(), true);

        if(patchReqDto.startDate() != null && patchReqDto.endDate() != null) {
            journey.changeStartDate(patchReqDto.startDate(), journey.getCityVisits());
            journey.changeEndDate(patchReqDto.endDate(), journey.getCityVisits());
        }
        if(patchReqDto.name() != null) {
            journey.changeName(patchReqDto.name());
        }
        if(patchReqDto.visibility() != null) {
            journey.modifyVisibility(patchReqDto.visibility());
        }
        return repository.save(journey);
    }

    @Override
    public void delete(Long memberId, Long journeyId) {
        Journey journey = findAndValidateOwner(memberId, journeyId, true);
        long count = journey.getCityVisits().stream().map(CityVisit::getId).toList()
                .stream().map(postQueryPort::detachByCityVisitId)
                .filter(result -> result)
                .count();
        if(count != journey.getCityVisits().size()) throw new IllegalStateException("Posts belong to CityVisit can not be deleted. (Unknown Error)");
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
