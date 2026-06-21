package kko.traveldiary_api.journey.domain;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.journey.domain.Journey;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityVisit {
    private Long id;
    private Journey journey;
    private Long cityId;
    private LocalDate startDate;
    private LocalDate endDate;
    @Builder
    private CityVisit(Long id, Journey journey, Long cityId, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.journey = journey;
        this.cityId = cityId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    void linkJourney(Journey journey) {
        this.journey = journey;
    }
}
