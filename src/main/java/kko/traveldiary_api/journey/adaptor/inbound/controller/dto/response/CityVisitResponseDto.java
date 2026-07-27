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
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String cityUrl;

    @Builder
    private CityVisitResponseDto(Long id, LocalDate startDate, LocalDate endDate, String cityUrl) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cityUrl = cityUrl;
    }

    public static CityVisitResponseDto convert(CityVisit cityVisit) {
        return CityVisitResponseDto.builder()
                .id(cityVisit.getCityId())
                .startDate(cityVisit.getStartDate())
                .endDate(cityVisit.getEndDate())
                .cityUrl("/api/city/" + cityVisit.getCityId())
                .build();
    }
}
