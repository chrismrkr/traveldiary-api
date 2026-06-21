package kko.traveldiary_api.journey.adaptor.infrastructure;

import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
class JourneyEntityTest {

    @Test
    @DisplayName("DB에서 조회한 Journey 엔티티를 도메인으로 변환할 수 있다")
    void convertJourneyEntityToDomain() {
        // given
        JourneyEntity entity = JourneyEntity.builder()
                .id(1L)
                .memberId(1L)
                .name("MyTestJourney")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .isActive(true)
                .visibility(Visibility.PUBLIC)
                .build();
        for(long idx=0; idx<3; idx++) {
            CityVisitEntity cityVisitEntity = new CityVisitEntity(idx, entity, 1L, LocalDate.now(), LocalDate.now());
            entity.getCityVisits().add(cityVisitEntity);
        }

        // when
        Journey domain = entity.toDomain();

        // then
        Assertions.assertEquals(domain.getId(), entity.getId());
        Assertions.assertEquals(domain.getMemberId(), entity.getMemberId());
        Assertions.assertEquals(domain.getName(), entity.getName());
        Assertions.assertEquals(domain.getIsActive(), entity.getIsActive());
        Assertions.assertEquals(domain.getVisibility(), entity.getVisibility());
        Assertions.assertEquals(domain.getCityVisitList().size(), 0);
    }

    @Test
    @DisplayName("DB에서 조회한 Journey 엔티티를 도메인으로 변환할 수 있다(@OneToMany 필드 함께 조회 가능)")
    void convertJourneyEntityToDomain2() {
        // given
        JourneyEntity entity = JourneyEntity.builder()
                .id(1L)
                .memberId(1L)
                .name("MyTestJourney")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .isActive(true)
                .visibility(Visibility.PUBLIC)
                .build();
        for(long idx=0; idx<3; idx++) {
            CityVisitEntity cityVisitEntity = new CityVisitEntity(idx, entity, 1L, LocalDate.now(), LocalDate.now());
            entity.getCityVisits().add(cityVisitEntity);
        }

        // when
        Journey domain = entity.toDomainWithCityVisits();

        // then
        Assertions.assertEquals(domain.getId(), entity.getId());
        Assertions.assertEquals(domain.getMemberId(), entity.getMemberId());
        Assertions.assertEquals(domain.getName(), entity.getName());
        Assertions.assertEquals(domain.getIsActive(), entity.getIsActive());
        Assertions.assertEquals(domain.getVisibility(), entity.getVisibility());
        Assertions.assertEquals(domain.getCityVisitList().size(), 3);
    }

    @Test
    @DisplayName("DB 영속을 위해 Journey 도메인을 엔티티로 변환할 수 있다 (@OneToMay 필드는 포함하지 않음)")
    void convertDomainToEntity() {
        // given
        Journey journey = Journey.builder().id(1L).memberId(1L)
                .name("Test Journey").startDate(LocalDate.now()).endDate(LocalDate.now())
                .createdAt(LocalDateTime.now()).lastModifiedAt(LocalDateTime.now()).isActive(true)
                .visibility(Visibility.PUBLIC).build();
        for(long idx=1; idx<=3; idx++) {
            journey.visit(
                    CityVisit.builder().id(idx).cityId(123L).
                    startDate(LocalDate.now()).endDate(LocalDate.now()).build());
        }

        // when
        JourneyEntity entity = JourneyEntity.from(journey);

        // then
        Assertions.assertEquals(journey.getId(), entity.getId());
        Assertions.assertEquals(journey.getMemberId(), entity.getMemberId());
        Assertions.assertEquals(journey.getName(), entity.getName());
        Assertions.assertEquals(journey.getCityVisitList().size(), 3);
        Assertions.assertEquals(entity.getCityVisits().size(), 0);
    }
}