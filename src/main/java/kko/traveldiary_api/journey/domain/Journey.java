package kko.traveldiary_api.journey.domain;

import io.swagger.v3.oas.annotations.links.Link;
import kko.traveldiary_api.journey.application.exception.InvalidCityVisitOrderException;
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
        cityVisit.changeOrder(this.cityVisits.size()); // 맨 뒤에 append (0-based)
        cityVisits.add(cityVisit);
        cityVisit.linkJourney(this.id);
        touch();
    }

    public void realignVisitOrder(List<CityVisitOrder> cityVisitOrders) {
        CityVisitOrder.validateOrder(cityVisitOrders);

        // full replace: 요청 id 집합이 현재 방문 집합과 정확히 일치해야 한다.
        Set<Long> requestIds = new HashSet<>();
        cityVisitOrders.forEach(o -> requestIds.add(o.cityVisitId()));
        Set<Long> currentIds = new HashSet<>();
        this.cityVisits.forEach(cv -> currentIds.add(cv.getId()));
        if (!requestIds.equals(currentIds)) {
            throw new InvalidCityVisitOrderException("요청한 방문 목록이 여행의 방문과 일치하지 않습니다");
        }

        // id -> CityVisit 인덱스 (O(1) 조회)
        Map<Long, CityVisit> byId = new HashMap<>();
        this.cityVisits.forEach(cv -> byId.put(cv.getId(), cv));

        // 요청 order 오름차순으로 정렬한 뒤 0..n-1 로 정규화한다.
        List<CityVisitOrder> sorted = new ArrayList<>(cityVisitOrders);
        sorted.sort(Comparator.comparingInt(CityVisitOrder::order));
        for (int i = 0; i < sorted.size(); i++) {
            byId.get(sorted.get(i).cityVisitId()).changeOrder(i);
        }
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
