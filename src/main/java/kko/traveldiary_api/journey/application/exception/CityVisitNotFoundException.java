package kko.traveldiary_api.journey.application.exception;

public class CityVisitNotFoundException extends IllegalArgumentException{
    private final Long cityVisitId;
    public CityVisitNotFoundException(Long cityVisitId) {
        super("CityVisit Not Found: " + cityVisitId);
        this.cityVisitId = cityVisitId;
    }
}
