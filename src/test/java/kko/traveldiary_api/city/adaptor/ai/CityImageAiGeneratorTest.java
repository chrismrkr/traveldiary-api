package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityImage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
@Slf4j
class CityImageAiGeneratorTest {
    @Autowired
    CityImageAiGenerator imageAiGenerator;
    @Value("${spring.ai.openai.api-key:NO_SET}")
    private String apiKey;

    @Test
    void 서울의_설명을_활용해서_이미지를_생성할_수_있다() throws IOException {
        // given
        log.info("apiKey: {}", apiKey);
        log.info("System Env: {}", System.getenv("OPENAI_API_KEY"));
        CityDescription seoul = new CityDescription("Seoul is a dynamic metropolis where ancient palaces stand beside gleaming skyscrapers, creating a captivating blend of tradition and cutting-edge modernity.",
                "As Korea's capital for over 600 years since the Joseon Dynasty, Seoul preserves royal palaces like Gyeongbokgung while embracing global pop culture through K-pop and Korean cinema, making it a cultural powerhouse.",
                "", "");
        // when
        long startTime = System.currentTimeMillis();
        CityImage seoulImage = imageAiGenerator.generate(seoul);
        log.info("It takes {} millis.", System.currentTimeMillis() - startTime);

        // then
        String filePath = "tmp/test-img/seoul.png";
        Path output = Path.of(filePath);
        Files.createDirectories(output.getParent());
        Files.write(output, seoulImage.imageBytes());
        Assertions.assertNotNull(seoulImage.imageBytes());
    }

    @Test
    void 포르투의_설명을_활용해서_이에_맞는_이미지를_생성할_수_있다() throws IOException {
        // given
        String explain = "Porto is a city of steep hills, colorful tiled facades, and the Douro River flowing beneath its iconic iron bridges—a place where old-world charm meets vibrant energy. Porto's history stretches back to Roman times, and it gave Portugal its very name. Famous for port wine production since the 17th century, its historic center is a UNESCO World Heritage Site filled with baroque churches, medieval streets, and azulejo-covered buildings.";
        log.info("apiKey: {}", apiKey);
        log.info("System Env: {}", System.getenv("OPENAI_API_KEY"));
        CityDescription porto = new CityDescription("Porto is a city of steep hills, colorful tiled facades, and the Douro River flowing beneath its iconic iron bridges—a place where old-world charm meets vibrant energy.",
                "Porto's history stretches back to Roman times, and it gave Portugal its very name. Famous for port wine production since the 17th century, its historic center is a UNESCO World Heritage Site filled with baroque churches, medieval streets, and azulejo-covered buildings.",
                "", "");

        // when
        long startTime = System.currentTimeMillis();
        CityImage portoImage = imageAiGenerator.generate(porto);
        log.info("It takes {} millis.", System.currentTimeMillis() - startTime);

        // then
        String filePath = "tmp/test-img/porto.png";
        Path output = Path.of(filePath);
        Files.createDirectories(output.getParent());
        Files.write(output, portoImage.imageBytes());
        Assertions.assertNotNull(portoImage.imageBytes());
    }

    @Test
    void 바르셀로나의의_설명을_활용해서_이에_맞는_이미지를_생성할_수_있다() throws IOException {
        // given
        String explain = "Barcelona is a vibrant Mediterranean city where modernist architecture meets beach culture, creating an energetic atmosphere that feels both artistic and laid-back. Founded as a Roman colony, Barcelona became the heart of Catalonia with its own distinct language and identity. The city flourished during the medieval period and later became synonymous with Gaudí's whimsical modernist masterpieces like the Sagrada Família.";
        log.info("apiKey: {}", apiKey);
        log.info("System Env: {}", System.getenv("OPENAI_API_KEY"));
        CityDescription barcelona = new CityDescription("Barcelona is a vibrant Mediterranean city where modernist architecture meets beach culture, creating an energetic atmosphere that feels both artistic and laid-back.",
                "The city flourished during the medieval period and later became synonymous with Gaudí's whimsical modernist masterpieces like the Sagrada Família.",
                "", "");

        // when
        long startTime = System.currentTimeMillis();
        CityImage portoImage = imageAiGenerator.generate(barcelona);
        log.info("It takes {} millis.", System.currentTimeMillis() - startTime);

        // then
        String filePath = "tmp/test-img/barcelona.png";
        Path output = Path.of(filePath);
        Files.createDirectories(output.getParent());
        Files.write(output, portoImage.imageBytes());
        Assertions.assertNotNull(portoImage.imageBytes());
    }

}