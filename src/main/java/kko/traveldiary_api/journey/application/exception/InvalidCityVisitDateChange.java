package kko.traveldiary_api.journey.application.exception;

public class InvalidCityVisitDateChange extends IllegalArgumentException {
    private String message;

    public InvalidCityVisitDateChange(String message) {
        super(message);
        this.message = message;
    }
}
