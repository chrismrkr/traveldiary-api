package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.application.required.CityDescriptionGenerator;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class CityDescriptionAIGenerator implements CityDescriptionGenerator {
    private final ChatClient claudeChatClient;

    @Autowired
    public CityDescriptionAIGenerator(@Qualifier("claudeChatClient") ChatClient claudeChatClient) {
        this.claudeChatClient = claudeChatClient;
    }

    @Override
    public CityDescription generate(City city) {
        assert city.getName() != null;
        assert city.getCoordinate() != null && city.getCoordinate().getLatitude() != null && city.getCoordinate().getLongitude() != null;

        return claudeChatClient.prompt()
                .system(CITY_DESCRIPTION_SYS_PROMPT)
                .user(u -> u.text("도시 이름: {name}, 위도: {latitude}, 경도: {longitude}")
                        .param("name", city.getName())
                        .param("latitude", city.getCoordinate().getLatitude())
                        .param("longitude", city.getCoordinate().getLongitude()))
                .call()
                .entity(CityDescription.class);
    }


    private static final String CITY_DESCRIPTION_SYS_PROMPT =
            "You are an expert who explains all the cities in the world." +
                    "Your response must always be in English." +
                    "You have deep knowledge of history, geography, culture, and more." +
                    "Explain things in a relaxed, casual tone — not overly academic or difficult for the user to follow." +
                    "Always use a polite, formal tone (no casual speech), and never use profanity." +
                    "Your response must be structured into exactly four parts: overview, historyAndCulture, funFact, and localTip." +
                    "overview: A short, one or two sentence impression of the city — the first thing someone would feel or notice about it." +
                    "historyAndCulture: A concise explanation of the city's historical background and cultural significance." +
                    "funFact: An interesting or lesser-known detail about the city. Keep it light, but never include anything that might offend local people." +
                    "localTip: A detail that reflects how locals actually experience or feel about the city — something a tourist might not notice." +
                    "Each part should be concise. Keep the total response across all four parts under 500 characters." +
                    "When the user provides a city name, latitude, and longitude, generate content for all four parts based on that information." +
                    "This description will also be used by another generative AI to visualize the explanation as an image.";
            ;

}
