package kko.traveldiary_api.journey.adaptor.infrastructure;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 도메인 {@link Journey} 의 영속성 엔티티.
 * 연관관계의 주인은 {@link CityVisitEntity} 측이며 CityVisit 은 독립적으로 영속된다.
 * 단, Journey 가 삭제될 때는 연관된 CityVisit 도 함께 제거된다(cascade REMOVE).
 */
@Entity
@Table(name = "journey")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JourneyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CityVisitEntity> cityVisits = new LinkedList<>();

    @Builder
    private JourneyEntity(Long id, Long memberId, String name, LocalDate startDate, LocalDate endDate,
                          LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean isActive, Visibility visibility, List<CityVisitEntity> cityVisits) {
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

    public static JourneyEntity from(Journey journey) {
        return JourneyEntity.builder()
                .id(journey.getId())
                .memberId(journey.getMemberId())
                .name(journey.getName())
                .startDate(journey.getStartDate())
                .endDate(journey.getEndDate())
                .createdAt(journey.getCreatedAt())
                .lastModifiedAt(journey.getLastModifiedAt())
                .isActive(journey.getIsActive())
                .visibility(journey.getVisibility())
                .build();
    }

    public Journey toDomain() {
        return Journey.builder()
                .id(id)
                .memberId(memberId)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(createdAt)
                .lastModifiedAt(lastModifiedAt)
                .isActive(isActive)
                .visibility(visibility)
                .build();
    }

    public Journey toDomainWithCityVisits() {
        List<CityVisit> cityVisits = new LinkedList<>();
        Journey journey = Journey.builder()
                .id(id)
                .memberId(memberId)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(createdAt)
                .lastModifiedAt(lastModifiedAt)
                .isActive(isActive)
                .visibility(visibility)
                .cityVisits(cityVisits)
                .build();
        this.cityVisits.forEach(
                cityVisit -> cityVisits.add(cityVisit.toDomain())
        );
        return journey;
    }

}
