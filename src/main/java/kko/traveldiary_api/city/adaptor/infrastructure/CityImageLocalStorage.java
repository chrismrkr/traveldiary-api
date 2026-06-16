package kko.traveldiary_api.city.adaptor.infrastructure;

import kko.traveldiary_api.city.application.required.CityImageStoragePort;
import org.springframework.stereotype.Component;

@Component
public class CityImageLocalStorage implements CityImageStoragePort {
    @Override
    public void save(String id, byte[] imageBytes) {

    }

    @Override
    public byte[] find(String id) {
        return new byte[0];
    }
}
