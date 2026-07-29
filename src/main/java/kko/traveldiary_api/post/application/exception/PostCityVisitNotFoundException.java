package kko.traveldiary_api.post.application.exception;

public class PostCityVisitNotFoundException extends IllegalArgumentException{
    private final Long cityVisitId;
    public PostCityVisitNotFoundException(Long cityVisitId) {
        super("CityVisit Not Exists: " + cityVisitId);
        this.cityVisitId= cityVisitId;
    }
}
