package kko.traveldiary_api.city.domain;

public record CityDescription (
        String overview,
        String historyAndCulture,
        String funFact,
        String localTip
) {
    public String getExplanation() {
        return overview + " " + historyAndCulture;
    }
}
