package kko.traveldiary_api.journey.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Builder
    private Journey(Long id, Long memberId, String name, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean isActive, Visibility visibility) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.isActive = isActive;
        this.visibility = visibility;
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
    }

    public void modifyName(String name) {
        this.name = name;
    }

    public void modifyVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void changeStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void changeEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
