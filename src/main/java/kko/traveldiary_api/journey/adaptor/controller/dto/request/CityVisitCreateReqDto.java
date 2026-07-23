package kko.traveldiary_api.journey.adaptor.controller.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CityVisitCreateReqDto(Long journeyId,
                                    String cityName, String cityId, BigDecimal latitude,
                                    BigDecimal longitude, LocalDate startDate, LocalDate endDate){
}
