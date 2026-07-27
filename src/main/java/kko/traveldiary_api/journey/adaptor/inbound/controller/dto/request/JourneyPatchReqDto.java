package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;


import java.time.LocalDate;

public record JourneyPatchReqDto(Long journeyId, LocalDate startDate,
                                 LocalDate endDate, String name, String visibility) { }
