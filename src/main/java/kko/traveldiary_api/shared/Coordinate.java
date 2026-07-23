package kko.traveldiary_api.shared;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Coordinate {
    private BigDecimal latitude;
    private BigDecimal longitude;
    public Coordinate(Double latitude, Double longitude) {
        this.latitude = BigDecimal.valueOf(latitude).setScale(6, RoundingMode.HALF_UP);
        this.longitude = BigDecimal.valueOf(longitude).setScale(6, RoundingMode.HALF_UP);
    }

    public Coordinate(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
