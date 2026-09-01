package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.application.required.CityImageGenerator;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityImage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;

import java.util.Base64;
import java.util.UUID;

// 수동 빈 등록: GenerativeAiConfig 에서 @Profile({"dev", "prod"}) 로 등록한다.
@RequiredArgsConstructor
public class CityImageAiGenerator implements CityImageGenerator {
    private final ImageModel imageModel;

    @Override
    public CityImage generate(City city, CityDescription description) {
        String imageFullPrompt = CITY_IMAGE_GEN_SYS_PROMPT
                + "City: " + city.getName() + ". "
                + "Description: " + description.getExplanation();
        ImageResponse image = imageModel.call(new ImagePrompt(imageFullPrompt));

        String imageId = UUID.randomUUID().toString();
        byte[] imageBytes = Base64.getDecoder().decode(image.getResult().getOutput().getB64Json());
        return new CityImage(imageId, imageBytes);
    }

    private static final String CITY_IMAGE_GEN_SYS_PROMPT =
            "A travel illustration of a city, based on the following description. " +
                    "Match the visual mood, color palette, and lighting to the atmosphere of this specific city " +
                    "as conveyed in the description — it may be vibrant, cool, moody, sunny, historic, or modern depending on the place. " +
                    "Painterly, slightly stylized travel poster aesthetic. " +
                    // 설명에 랜드마크가 없는 도시도 있으므로, 없을 때의 대안을 함께 준다.
                    "If the description names a specific landmark, make it the clear focal point " +
                    "and render it recognisably. If it names none, build the composition from the " +
                    "setting, architecture, and street life the description mentions instead. " +
                    "Do not add famous landmarks that the description does not mention. " +
                    // 포스터 화풍은 학습 데이터상 타이포그래피와 강하게 붙어 있어, 금지를 구체적으로 나열해야 한다.
                    "Render the scene only: absolutely no text, letters, words, numbers, captions, " +
                    "signage, logos, watermarks, or signatures anywhere in the image. ";
}
