package kko.traveldiary_api.city.application.required;

public interface CityImageStoragePort {
    void save(String id, byte[] imageBytes);
    byte[] find(String id);
}
