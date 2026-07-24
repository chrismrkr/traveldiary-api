package kko.traveldiary_api.journey.domain;

import io.swagger.v3.oas.annotations.links.Link;
import kko.traveldiary_api.journey.application.exception.InvalidJourneyDateChangeException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Journey {
    private Long id;
    private Long memberId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private Boolean isActive;
    private Visibility visibility;

    private List<CityVisit> cityVisits = new LinkedList<>();

    @Builder
    private Journey(Long id, Long memberId, String name, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean isActive, Visibility visibility, List<CityVisit> cityVisits) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.isActive = isActive;
        this.visibility = visibility;
        this.cityVisits = cityVisits == null ? new LinkedList<>() : cityVisits;
    }

    public static Journey create(Long memberId, String name, LocalDate startDate, LocalDate endDate, Visibility visibility) {
        return Journey.builder()
                .memberId(memberId)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .isActive(true)
                .visibility(visibility).build();
    }

    public void deactivate() {
        this.isActive = false;
        touch();
    }

    public void modifyVisibility(Visibility visibility) {
        this.visibility = visibility;
        touch();
    }

    public void changeStartDate(LocalDate startDate, List<CityVisit> cityVisitList) {
        for(CityVisit cityVisit : cityVisitList) {
            if(cityVisit.getStartDate().isBefore(startDate)) {
                throw new InvalidJourneyDateChangeException("Journey 시작일 보다 도시 방문 시작일이 이전일 수 없음");
            }
        }
        this.startDate = startDate;
        touch();
    }

    public void changeEndDate(LocalDate endDate, List<CityVisit> cityVisitList) {
        for(CityVisit cityVisit : cityVisitList) {
            if(cityVisit.getEndDate().isAfter(endDate)) {
                throw new InvalidJourneyDateChangeException("도시 방문 종료일 보다 Journey 종료일이 빠를 수 없음");
            }
        }
        this.endDate = endDate;
        touch();
    }

    public void changeName(String name) {
        this.name = name;
        touch();
    }

    public void visit(CityVisit cityVisit) {
        cityVisits.add(cityVisit);
        cityVisit.linkJourney(this.id);
        touch();
    }

    public boolean isOwnedBy(Long memberId) {
        return this.getMemberId().equals(memberId);
    }

    public Optional<CityVisit> findCityVisitById(Long cityVisitId) {
        return this.getCityVisits().stream()
                .filter(cityVisit -> cityVisit.getId().equals(cityVisitId))
                .findAny();
    }

    private void touch() {
        this.lastModifiedAt = LocalDateTime.now();
    }
}
