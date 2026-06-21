package kko.traveldiary_api.city.adaptor.infrastructure.db;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kko.traveldiary_api.shared.Coordinate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 도메인 VO {@link Coordinate} 의 영속성 표현.
 * 공유 도메인 타입을 JPA 에 의존시키지 않기 위해 어댑터 계층에 둔다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoordinateEmbeddable {

    // 위도: -90 ~ 90, 경도: -180 ~ 180, 소수점 6자리 → precision 9 / scale 6
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    private CoordinateEmbeddable(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    static CoordinateEmbeddable fromDomain(Coordinate coordinate) {
        return new CoordinateEmbeddable(coordinate.getLatitude(), coordinate.getLongitude());
    }

    Coordinate toDomain() {
        return new Coordinate(latitude.doubleValue(), longitude.doubleValue());
    }
}
