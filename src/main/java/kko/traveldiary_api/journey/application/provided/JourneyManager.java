package kko.traveldiary_api.journey.application.provided;

import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyRegisterReqDto;

import java.time.LocalDate;

public interface JourneyManager {
    Journey register(JourneyRegisterReqDto registerDto);
    Journey modify(JourneyPatchReqDto patchDto);
    void delete(Long journeyId);
}
