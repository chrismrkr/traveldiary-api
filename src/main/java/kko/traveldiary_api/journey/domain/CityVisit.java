package kko.traveldiary_api.journey.domain;


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

    public void changeStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void changeEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    void linkJourney(Journey journey) {
        this.journey = journey;
    }

    public void validateVisitedDate(Journey journey, LocalDate startDate, LocalDate endDate) {
        if(startDate.isBefore(journey.getStartDate())) {
            throw new IllegalArgumentException("도시 방문일는 여행 시작일 이전일 수 없음");
        }
        if(endDate.isAfter(journey.getEndDate())) {
            throw new IllegalArgumentException("도시 방문일은 여행 종료일 이후일 수 없음");
        }
    }
}
