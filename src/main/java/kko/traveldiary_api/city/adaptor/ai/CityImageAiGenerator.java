package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.application.required.CityImageGenerator;
import kko.traveldiary_api.city.domain.CityImage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CityImageAiGenerator implements CityImageGenerator {
    private final ImageModel imageModel;

    @Override
    public CityImage generate(String description) {
        String imageFullPrompt = CITY_IMAGE_GEN_SYS_PROMPT + description;
        ImageResponse image = imageModel.call(
                new ImagePrompt(imageFullPrompt,
                        OpenAiImageOptions.builder()
                                .model("dall-e-3")
                                .quality("standard")
                                .width(1024).height(1024)
                                .build()));

        String imageId = UUID.randomUUID().toString();
        byte[] imageBytes = Base64.getDecoder().decode(image.getResult().getOutput().getB64Json());
        return new CityImage(imageId, imageBytes);
    }

    private static final String CITY_IMAGE_GEN_SYS_PROMPT =
            "당신은 전세계 도시를 알고 있습니다. " +
                    "적절한 도시에 대한 설명을 들었을 때, 그것을 이미지로 형상화할 수 있습니다. 설명: ";
}
