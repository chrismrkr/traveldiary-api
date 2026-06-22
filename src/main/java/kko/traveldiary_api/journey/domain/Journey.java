package kko.traveldiary_api.journey.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    private List<CityVisit> cityVisitList = new ArrayList<>();

    @Builder
    private Journey(Long id, Long memberId, String name, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean isActive, Visibility visibility, List<CityVisit> cityVisitList) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.isActive = isActive;
        this.visibility = visibility;
        this.cityVisitList = cityVisitList == null ? new ArrayList<>() : cityVisitList;
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

    public void modifyName(String name) {
        this.name = name;
        touch();
    }

    public void modifyVisibility(Visibility visibility) {
        this.visibility = visibility;
        touch();
    }

    public void changeStartDate(LocalDate startDate, List<CityVisit> cityVisitList) {
        for(CityVisit cityVisit : cityVisitList) {
            if(cityVisit.getStartDate().isBefore(startDate)) {
                throw new IllegalArgumentException("Journey 시작일 보다 도시 방문 시작일이 이전일 수 없음");
            }
        }
        this.startDate = startDate;
        touch();
    }

    public void changeEndDate(LocalDate endDate, List<CityVisit> cityVisitList) {
        for(CityVisit cityVisit : cityVisitList) {
            if(cityVisit.getEndDate().isAfter(endDate)) {
                throw new IllegalArgumentException("도시 방문 종료일 보다 Journey 종료일이 빠를 수 없음");
            }
        }
        this.endDate = endDate;
        touch();
    }

    public void visit(CityVisit cityVisit) {
        cityVisitList.add(cityVisit);
        cityVisit.linkJourney(this);
        touch();
    }

    private void touch() {
        this.lastModifiedAt = LocalDateTime.now();
    }
}
