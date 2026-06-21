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
import kko.traveldiary_api.journey.domain.Journey;
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

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public CityVisitEntity(Long id, JourneyEntity journey, Long cityId, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.journey = journey;
        this.cityId = cityId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CityVisitEntity from(CityVisit cityVisit) {
        return new CityVisitEntity(
                cityVisit.getId(),
                JourneyEntity.from(cityVisit.getJourney()),
                cityVisit.getCityId(),
                cityVisit.getStartDate(),
                cityVisit.getEndDate()
        );
    }

    /**
     * 엔티티 -> 도메인 변환. 소속 Journey 는 자식 컬렉션 없이 얕게 복원한다.
     * (Journey 를 재구성하지 않으므로 Journey -> CityVisit -> Journey 순환이 발생하지 않는다.)
     */
    CityVisit toDomain() {
        return CityVisit.builder()
                .id(id)
                .journey(
                        Journey.builder()
                                .id(journey.getId()).memberId(journey.getMemberId()).name(journey.getName())
                                .createdAt(journey.getCreatedAt()).lastModifiedAt(journey.getLastModifiedAt())
                                .startDate(journey.getStartDate()).endDate(journey.getEndDate())
                                .isActive(journey.getIsActive()).visibility(journey.getVisibility())
                                .build()
                )
                .cityId(cityId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
