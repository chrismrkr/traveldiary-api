package kko.traveldiary_api.city.application.required;

import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityImage;

public interface CityImageGenerator {
    CityImage generate(CityDescription description);
}
