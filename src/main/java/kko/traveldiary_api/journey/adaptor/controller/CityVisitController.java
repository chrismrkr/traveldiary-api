package kko.traveldiary_api.journey.adaptor.controller;

import kko.traveldiary_api.journey.application.provided.CityVisitManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CityVisitController {
    private final CityVisitManager cityVisitManager;

    // 여행 중 도시 방문 이력 등록 요청

    // 도시 방문 수정 (방문-종료 일자 / 방문순서)

    // 도시 방문 이력 삭제
}
