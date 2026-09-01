package kko.traveldiary_api.city.application.required;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityImage;

public interface CityImageGenerator {
    /**
     * 설명만으로는 어느 도시인지 특정되지 않을 수 있어(설명이 도시명을 언급하지 않는 경우)
     * 도시를 함께 받아 프롬프트에 이름을 명시한다.
     */
    CityImage generate(City city, CityDescription description);
}
