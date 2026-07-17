package kko.traveldiary_api.journey.application.exception;

public class InvalidJourneyDateChangeException extends IllegalArgumentException {
    public InvalidJourneyDateChangeException() {
    }

    public InvalidJourneyDateChangeException(String s) {
        super(s);
    }
}
