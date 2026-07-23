package kko.traveldiary_api.journey.domain;


import kko.traveldiary_api.journey.application.exception.InvalidCityVisitDateChange;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityVisit {
    private Long id;
    private Long journeyId;
    private Long cityId;
    private int visitOrder;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    private CityVisit(Long id, Long journeyId, Long cityId, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.journeyId = journeyId;
        this.cityId = cityId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void changeStartDate(LocalDate startDate, LocalDate journeyStartDate) {
        if(startDate.isBefore(journeyStartDate)) {
            throw new InvalidCityVisitDateChange("도시 방문일는 여행 시작일 이전일 수 없음");
        }
        this.startDate = startDate;
    }

    public void changeEndDate(LocalDate endDate, LocalDate journeyEndDate) {
        if(endDate.isAfter(journeyEndDate)) {
            throw new InvalidCityVisitDateChange("도시 방문일은 여행 종료일 이후일 수 없음");
        }
        this.endDate = endDate;
    }

    void linkJourney(Long journeyId) {
        this.journeyId = journeyId;
    }

    public void validateVisitedDate(LocalDate endDate, LocalDate journeyEndDate) {
        if(endDate.isAfter(journeyEndDate)) {
            throw new IllegalArgumentException("도시 방문일은 여행 종료일 이후일 수 없음");
        }
    }
}
