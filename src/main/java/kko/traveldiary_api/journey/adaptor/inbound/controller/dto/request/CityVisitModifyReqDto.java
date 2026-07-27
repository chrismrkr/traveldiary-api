package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;

import java.time.LocalDate;

public record CityVisitModifyReqDto(Long journeyId, Long cityVisitId,
                                    LocalDate startDate, LocalDate endDate) {
}
