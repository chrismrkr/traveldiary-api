package kko.traveldiary_api.journey.application.exception;

public class InvalidCityVisitOrderException extends IllegalArgumentException {
    public InvalidCityVisitOrderException(String message) {
        super(message);
    }
}
