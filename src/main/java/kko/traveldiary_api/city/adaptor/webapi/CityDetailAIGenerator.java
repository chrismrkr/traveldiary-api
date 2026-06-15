package kko.traveldiary_api.city.adaptor.webapi;

import kko.traveldiary_api.city.application.provided.CityImageAdministrator;
import kko.traveldiary_api.city.application.provided.CityRegistration;
import kko.traveldiary_api.city.application.required.CityDetailGenerator;
import kko.traveldiary_api.city.domain.City;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CityDetailAIGenerator implements CityDetailGenerator {
    private final ChatClient chatClient;
    private final CityRegistration cityRegistration;
    private final CityImageAdministrator cityImageAdministrator;
    @Override
    public City generateDetail(City city) {
        // City Description 생성
        CityDescription description  = chatClient.prompt()
                .system(CITY_DESCRIPTION_SYS_PROMPT)
                .user(u -> u.text("도시 이름: {name}, 위도: {latitude}, 경도: {longitude}")
                        .param("name", city.getName())
                        .param("latitude", city.getCoordinate().getLatitude())
                        .param("longitude", city.getCoordinate().getLongitude()))
                .call()
                .entity(CityDescription.class);

        // City Description을 바탕으로 이미지 생성


        // CityDescription 이미지 저장
        String imageId = UUID.randomUUID().toString();


        city.setReady();
        cityRegistration.register();
    }

    record CityDescription (
            String summary
    ) {}


    private static final String CITY_DESCRIPTION_SYS_PROMPT =
            "당신은 전세계에 존재하는 모든 도시에 대해서 설명해주는 전문가 입니다." +
            "역사, 지리, 문화 등에 대해서 잘 알고 있습니다." +
            "사용자에게 너무 어렵게 설명하지 말고 적당히 편안하고 캐주얼한 톤으로 설명해야 합니다." +
            "설명은 그 도시에 대해 사람들이 보편적으로 느끼는 것을 담고 있어야 합니다." +
            "설명은 항상 존댓말을 사용하고, 비속어 사용은 절대 하면 안됩니다." +
            "하지만, 너무 공손하게 할 필요도 없고 적당히 공손한 수준해서 캐주얼하게 설명하면 됩니다." +
            "사용자가 도시 이름, 위도, 경도 3가지를 전달하면 이에 맞는 적절한 설명을 제공한다." +
            "그리고 너가 하는 설명은 또 다른 생성형 AI를 통해서 설명을 이미지로 형상화하는데 사용된다.";

    private static final String CITY_IMAGE_GEN_SYS_PROMPT =
            "당신은 전세계 도시를 알고 있습니다." +
            "적절한 도시에 대한 설명을 들었을 때, 그것을 이미지로 형상화할 수 있습니다.";

}
