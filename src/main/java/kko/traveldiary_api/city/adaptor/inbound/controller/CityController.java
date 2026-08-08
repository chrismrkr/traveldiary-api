package kko.traveldiary_api.city.adaptor.inbound.controller;

import kko.traveldiary_api.city.adaptor.inbound.controller.dto.response.CityResponseDto;
import kko.traveldiary_api.city.adaptor.inbound.controller.dto.response.CityResponseStatuses;
import kko.traveldiary_api.city.adaptor.inbound.controller.dto.response.CommonCityResponse;
import kko.traveldiary_api.city.application.provided.CityFinder;
import kko.traveldiary_api.city.domain.City;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class CityController {
    private final CityFinder cityFinder;

    /** 도시 이미지 응답의 브라우저 캐시 max-age (기본 30일) */
    @Value("${app.city.image.cache-ttl:P30D}")
    private Duration imageCacheTtl;

    // 프론트엔드 Map 이 제공하는 placeId 로 도시 정보를 조회한다.
    @GetMapping("/api/city/{placeId}")
    public ResponseEntity<CommonCityResponse<CityResponseDto>> handleFindCityByPlaceIdRequest(
            @PathVariable("placeId") String placeId) {
        City city = cityFinder.findByPlaceId(placeId);
        return ResponseEntity.ok(new CommonCityResponse<>(
                CityResponseStatuses.SUCCESS, CityResponseDto.convert(city)));
    }

    // 도시 배경 이미지를 원본 바이트로 반환한다. (<img src> 로 직접 사용)
    @GetMapping("/api/city/image/{imageId}")
    public ResponseEntity<byte[]> handleFindCityImageRequest(@PathVariable("imageId") String imageId) {
        byte[] image = cityFinder.findImage(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(imageCacheTtl).cachePublic().immutable())
                .body(image);
    }
}
