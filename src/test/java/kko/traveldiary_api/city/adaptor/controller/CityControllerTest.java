package kko.traveldiary_api.city.adaptor.controller;

import kko.traveldiary_api.city.adaptor.infrastructure.db.CityJpaRepository;
import kko.traveldiary_api.city.application.required.CityImageStoragePort;
import kko.traveldiary_api.city.application.required.CityRepository;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CityControllerTest {

    private static final Path STORAGE_DIR =
            Path.of(System.getProperty("java.io.tmpdir"), "traveldiary-test-" + UUID.randomUUID());

    @DynamicPropertySource
    static void overrideStoragePath(DynamicPropertyRegistry registry) {
        registry.add("app.city.image.storage-path", STORAGE_DIR::toString);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    CityJpaRepository cityJpaRepository;

    @Autowired
    CityImageStoragePort imageStorage;

    @BeforeEach
    @AfterEach
    void clean() throws IOException {
        cityJpaRepository.deleteAll();
        if (Files.exists(STORAGE_DIR)) {
            try (var paths = Files.walk(STORAGE_DIR)) {
                paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignored) { }
                });
            }
        }
    }

    private static RequestPostProcessor accessToken(Long memberId) {
        return jwt().jwt(builder -> builder.subject(String.valueOf(memberId)));
    }

    private City saveReadyCity(String placeId, String name, Coordinate coordinate, String imageId) {
        return cityRepository.save(City.builder()
                .name(name)
                .placeId(placeId)
                .cityDescription(new CityDescription("개요", "역사와 문화", "재미있는 사실", "현지 팁"))
                .cityImageId(imageId)
                .coordinate(coordinate)
                .status(City.Status.READY)
                .build());
    }

    @Test
    @DisplayName("GET /api/city/{placeId} - placeId로 도시 정보를 조회한다")
    void findByPlaceId() throws Exception {
        saveReadyCity("place-tokyo", "Tokyo", new Coordinate(35.6762, 139.6503), "img-tokyo");

        mockMvc.perform(get("/api/city/{placeId}", "place-tokyo")
                        .with(accessToken(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.placeId").value("place-tokyo"))
                .andExpect(jsonPath("$.data.name").value("Tokyo"))
                .andExpect(jsonPath("$.data.cityImageId").value("img-tokyo"))
                .andExpect(jsonPath("$.data.overview").value("개요"));
    }

    @Test
    @DisplayName("GET /api/city/{placeId} - 등록되지 않은 placeId면 404")
    void findByPlaceId_notFound() throws Exception {
        mockMvc.perform(get("/api/city/{placeId}", "place-unknown")
                        .with(accessToken(1L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/city/image/{imageId} - 인증 없이 원본 PNG 바이트를 반환한다")
    void findImage() throws Exception {
        byte[] bytes = {1, 2, 3, 4, 5};
        imageStorage.save("img-1", bytes);

        byte[] body = mockMvc.perform(get("/api/city/image/{imageId}", "img-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn().getResponse().getContentAsByteArray();

        assertThat(body).isEqualTo(bytes);
    }

    @Test
    @DisplayName("GET /api/city/image/{imageId} - 없는 이미지면 404")
    void findImage_notFound() throws Exception {
        mockMvc.perform(get("/api/city/image/{imageId}", "missing"))
                .andExpect(status().isNotFound());
    }
}
