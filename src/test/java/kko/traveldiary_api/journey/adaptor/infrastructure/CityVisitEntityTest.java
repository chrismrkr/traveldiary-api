package kko.traveldiary_api.journey.adaptor.infrastructure;

import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CityVisitEntityTest {

    @Test
    @DisplayName("DB에서 조회한 CityVisit 엔티리를 도메인으로 변환할 수 있다.")
    void convertCityVisitEntityToDomain() {
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
        CityVisitEntity cityVisitEntity = new CityVisitEntity(0L, entity, 1L, 0, LocalDate.now(), LocalDate.now());
        entity.getCityVisits().add(cityVisitEntity);

        // when
        CityVisit cityVisit = cityVisitEntity.toDomain();

        // when
        Assertions.assertEquals(cityVisitEntity.getId(), cityVisit.getId());
    }

    @Test
    @DisplayName("DB 영속을 위해 CityVisit 도메인을 엔티티로 변환할 수 있다 (@ManyToOne 필드 Id 반드시 포함 필요)")
    void convertDomainToEntity() {
        // given
        Journey journey = Journey.builder().id(1L).memberId(1L)
                .name("TestJourney").startDate(LocalDate.now()).endDate(LocalDate.now())
                .createdAt(LocalDateTime.now()).lastModifiedAt(LocalDateTime.now()).isActive(true)
                .visibility(Visibility.PUBLIC).build();

        // when
        CityVisit cityVisit = CityVisit.builder().id(1L).cityId(123L).
                startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        journey.visit(cityVisit);
        CityVisitEntity cityVisitEntity = CityVisitEntity.from(cityVisit);

        // then
        Assertions.assertEquals(cityVisit.getId(), cityVisitEntity.getId());
        Assertions.assertEquals(cityVisit.getCityId(), cityVisitEntity.getCityId());
        Assertions.assertEquals(cityVisit.getStartDate(), cityVisitEntity.getStartDate());
        Assertions.assertEquals(cityVisit.getEndDate(), cityVisitEntity.getEndDate());
        Assertions.assertEquals(cityVisit.getJourneyId(), cityVisitEntity.getJourney().getId());
    }

}