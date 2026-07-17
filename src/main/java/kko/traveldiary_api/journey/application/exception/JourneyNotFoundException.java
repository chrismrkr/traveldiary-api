package kko.traveldiary_api.journey.application.exception;

public class JourneyNotFoundException extends IllegalArgumentException {
    private final Long journeyId;
    public JourneyNotFoundException(Long journeyId) {
        super("Journey Not Found: " + journeyId);
        this.journeyId = journeyId;
    }
}
