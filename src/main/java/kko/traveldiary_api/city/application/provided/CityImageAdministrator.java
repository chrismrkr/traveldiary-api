package kko.traveldiary_api.city.application.provided;

public interface CityImageAdministrator {
    void save(String id, byte[] imageBytes);
    byte[] find(String id);
}
