package kko.traveldiary_api.city.adaptor.infrastructure.db;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kko.traveldiary_api.city.domain.CityDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도메인 VO {@link CityDescription} 의 영속성 표현.
 * 공유 도메인 타입을 JPA 에 의존시키지 않기 위해 어댑터 계층에 둔다.
 * AI 상세 생성 전(PENDING)에는 값이 비어 있을 수 있어 null 을 허용한다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityDescriptionEmbeddable {

    @Column(name = "description_overview", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "description_history_and_culture", columnDefinition = "TEXT")
    private String historyAndCulture;

    @Column(name = "description_fun_fact", columnDefinition = "TEXT")
    private String funFact;

    @Column(name = "description_local_tip", columnDefinition = "TEXT")
    private String localTip;

    private CityDescriptionEmbeddable(String overview, String historyAndCulture,
                                      String funFact, String localTip) {
        this.overview = overview;
        this.historyAndCulture = historyAndCulture;
        this.funFact = funFact;
        this.localTip = localTip;
    }

    static CityDescriptionEmbeddable fromDomain(CityDescription description) {
        if (description == null) {
            return null;
        }
        return new CityDescriptionEmbeddable(
                description.overview(),
                description.historyAndCulture(),
                description.funFact(),
                description.localTip()
        );
    }

    CityDescription toDomain() {
        return new CityDescription(overview, historyAndCulture, funFact, localTip);
    }
}
