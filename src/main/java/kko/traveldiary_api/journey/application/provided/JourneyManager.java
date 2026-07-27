package kko.traveldiary_api.journey.application.provided;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyRegisterReqDto;

public interface JourneyManager {
    Journey register(JourneyRegisterReqDto registerDto);
    Journey modify(Long memberId, JourneyPatchReqDto patchDto);
    void delete(Long memberId, Long journeyId);
}
