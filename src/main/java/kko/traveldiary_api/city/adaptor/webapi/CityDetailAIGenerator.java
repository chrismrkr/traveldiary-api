package kko.traveldiary_api.city.adaptor.webapi;

import kko.traveldiary_api.city.application.provided.CityRegistration;
import kko.traveldiary_api.city.application.required.CityDetailGenerator;
import kko.traveldiary_api.city.domain.City;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityDetailAIGenerator implements CityDetailGenerator {
    private final ChatClient chatClient;
    private final CityRegistration cityRegistration;
    @Override
    public City generateDetail(City city) {
        return null;
    }


}
