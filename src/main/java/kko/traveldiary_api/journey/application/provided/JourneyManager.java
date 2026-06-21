package kko.traveldiary_api.journey.application.provided;

import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import kko.traveldiary_api.journey.dto.JourneyRegisterDto;

import java.time.LocalDate;

public interface JourneyManager {
    Journey register(JourneyRegisterDto registerDto);
    Journey modifyDate(Long journeyId, LocalDate startDate, LocalDate endDate);
    void adjustVisibility(Long id, Visibility visibility);
}
