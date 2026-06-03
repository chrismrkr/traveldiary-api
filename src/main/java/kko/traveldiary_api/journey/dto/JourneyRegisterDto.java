package kko.traveldiary_api.journey.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JourneyRegisterDto {
    private Long memberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private String visibility;
}
