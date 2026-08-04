package kko.traveldiary_api.city.adaptor.inbound.controller.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommonCityResponse<T> {
    private CityResponseStatuses status;
    private T data;
}
