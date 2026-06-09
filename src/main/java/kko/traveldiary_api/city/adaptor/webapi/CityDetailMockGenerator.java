package kko.traveldiary_api.city.adaptor.webapi;

import kko.traveldiary_api.city.application.required.CityDetailGenerator;
import kko.traveldiary_api.city.domain.City;
import org.springframework.stereotype.Component;

@Component
public class CityDetailMockGenerator implements CityDetailGenerator {
    public static final String MOCK_DESCRIPTION = "Mock Generator Did Something";
    public static final String MOCK_IMG_ID = "Mock123-123-0000";
    @Override
    public City generateDetail(City city) {
        city.setDetails(MOCK_DESCRIPTION, MOCK_IMG_ID);
        return city;
    }
}
