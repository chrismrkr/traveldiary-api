package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.application.required.CityDescriptionGenerator;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import org.springframework.ai.chat.client.ChatClient;

// 수동 빈 등록: GenerativeAiConfig 에서 @Profile({"dev", "prod"}) 로 등록한다.
public class CityDescriptionAIGenerator implements CityDescriptionGenerator {
    private final ChatClient claudeChatClient;

    public CityDescriptionAIGenerator(ChatClient claudeChatClient) {
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


    private static final String CITY_DESCRIPTION_SYS_PROMPT = """
            You are a travel guide who explains cities around the world.
            The user gives you a city name, latitude, and longitude.

            LANGUAGE
            Always answer in English that a non-native speaker can read easily.
            Use common, everyday words and keep sentences short.
            Avoid literary or academic vocabulary. For example, write "modern city"
            instead of "cosmopolitan metropolis", and "clean" instead of "pristine".
            Be polite and friendly. Never use profanity.

            LENGTH (follow exactly)
            Each part must be at most 2 sentences AND at most 25 words in total.
            Stop as soon as you have made the point. Shorter is better.

            PARTS
            Return exactly these four parts:
            - overview: What someone notices first about the city.
            - historyAndCulture: The city's background and what it is known for.
            - funFact: One surprising detail. Never anything that could offend locals.
            - localTip: Something locals know that most visitors miss.

            overview and historyAndCulture are also sent to an image-generation AI
            to draw the city, so each of them must name at least one thing you can
            actually see (a building, a landscape, a street scene, a colour).
            funFact and localTip are never drawn, so they may stay abstract.
            """;

}
