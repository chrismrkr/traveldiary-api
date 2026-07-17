package kko.traveldiary_api.journey.application.exception;

public class JourneyAccessDeniedException extends IllegalArgumentException {
    private final Long memberId;
    private final Long journeyId;

    public JourneyAccessDeniedException(Long memberId, Long journeyId) {
        super("Journey Access Denied: Member Not Own Journey");
        this.memberId = memberId;
        this.journeyId = journeyId;
    }
}
