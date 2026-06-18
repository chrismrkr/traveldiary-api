package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.application.required.CityImageGenerator;
import kko.traveldiary_api.city.domain.CityDescription;
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
    public CityImage generate(CityDescription description) {
        String imageFullPrompt = CITY_IMAGE_GEN_SYS_PROMPT + description.getExplanation();
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
                    "No text or letters in the image. Description: ";
}
