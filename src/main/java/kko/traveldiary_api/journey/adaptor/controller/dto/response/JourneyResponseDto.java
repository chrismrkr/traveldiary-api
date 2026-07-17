package kko.traveldiary_api.journey.adaptor.controller.dto.response;

import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JourneyResponseDto {
    private Long journeyId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private Boolean isActive;
    private Visibility visibility;
    private List<CityVisitResponseDto> cityVisits = new ArrayList<>();

    @Builder
    private JourneyResponseDto(Long journeyId, String name, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean isActive, Visibility visibility) {
        this.journeyId = journeyId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.isActive = isActive;
        this.visibility = visibility;
    }

    public static JourneyResponseDto convert(Journey journey) {
        JourneyResponseDto dto = JourneyResponseDto.builder()
                .journeyId(journey.getId())
                .name(journey.getName())
                .startDate(journey.getStartDate())
                .endDate(journey.getEndDate())
                .createdAt(journey.getCreatedAt())
                .lastModifiedAt(journey.getLastModifiedAt())
                .isActive(journey.getIsActive())
                .visibility(journey.getVisibility())
                .build();
        if(!journey.getCityVisits().isEmpty()) {
            journey.getCityVisits().forEach(cityVisit ->
                    dto.cityVisits.add(CityVisitResponseDto.convert(cityVisit)));
        }
        return dto;
    }
}
