package kko.traveldiary_api.city.domain;

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
    private City city;
    private LocalDate visitDate;
    @Builder
    private CityVisit(Long id, Journey journey, City city, LocalDate visitDate) {
        this.id = id;
        this.journey = journey;
        this.city = city;
        this.visitDate = visitDate;
    }
}
