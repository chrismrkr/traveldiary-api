package kko.traveldiary_api.journey.domain;

import kko.traveldiary_api.journey.application.exception.InvalidCityVisitOrderException;

import javax.validation.constraints.NotNull;
import java.util.List;

public record CityVisitOrder(
        @NotNull
        Long cityVisitId,

        @NotNull
        Integer order) {
    public static void validateOrder(List<CityVisitOrder> cityVisitOrders) {
        long distinctIds = cityVisitOrders.stream().map(CityVisitOrder::cityVisitId).distinct().count();
        if (distinctIds != cityVisitOrders.size()) {
            throw new InvalidCityVisitOrderException("Invalid Request: Duplicated cityVisitId exists");
        }
        long distinctOrders = cityVisitOrders.stream().map(CityVisitOrder::order).distinct().count();
        if (distinctOrders != cityVisitOrders.size()) {
            throw new InvalidCityVisitOrderException("Invalid Request: Duplicated Order exists");
        }
    }
}
