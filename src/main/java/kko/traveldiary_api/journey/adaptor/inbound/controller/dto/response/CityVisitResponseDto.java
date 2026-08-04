package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response;

import kko.traveldiary_api.journey.domain.CityVisit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityVisitResponseDto {
    private Long cityVisitId;
    private Long cityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String cityUrl;

    @Builder
    private CityVisitResponseDto(Long cityVisitId, Long cityId, LocalDate startDate, LocalDate endDate, String cityUrl) {
        this.cityVisitId = cityVisitId;
        this.cityId = cityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cityUrl = cityUrl;
    }

    public static CityVisitResponseDto convert(CityVisit cityVisit) {
        return CityVisitResponseDto.builder()
                .cityVisitId(cityVisit.getId())
                .cityId(cityVisit.getCityId())
                .startDate(cityVisit.getStartDate())
                .endDate(cityVisit.getEndDate())
                .cityUrl("/api/city/" + cityVisit.getCityId())
                .build();
    }
}
