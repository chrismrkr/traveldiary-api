package kko.traveldiary_api.journey.adaptor.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kko.traveldiary_api.journey.domain.CityVisit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 도메인 {@link CityVisit} 의 영속성 엔티티.
 * Journey 애그리거트에 속하며, 방문 도시는 City 애그리거트를 id({@code cityId}) 로만 참조한다.
 */
@Entity
@Table(name = "city_visit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityVisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id", nullable = false)
    private JourneyEntity journey;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "visit_order", nullable = false)
    private int visitOrder;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public CityVisitEntity(Long id, JourneyEntity journey, Long cityId, int visitOrder,
                           LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.journey = journey;
        this.cityId = cityId;
        this.visitOrder = visitOrder;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CityVisitEntity from(CityVisit cityVisit) {
        return new CityVisitEntity(
                cityVisit.getId(),
                JourneyEntity.reference(cityVisit.getJourneyId()),
                cityVisit.getCityId(),
                cityVisit.getVisitOrder(),
                cityVisit.getStartDate(),
                cityVisit.getEndDate()
        );
    }

    /**
     * 엔티티 -> 도메인 변환. 부모 Journey 는 id 로만 참조한다.
     * (lazy 프록시라도 getId() 는 초기화를 유발하지 않아 트랜잭션 밖에서도 안전하다.)
     */
    CityVisit toDomain() {
        return CityVisit.builder()
                .id(id)
                .journeyId(journey.getId())
                .cityId(cityId)
                .visitOrder(visitOrder)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
