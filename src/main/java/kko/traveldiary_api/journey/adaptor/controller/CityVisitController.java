package kko.traveldiary_api.journey.adaptor.controller;

import kko.traveldiary_api.journey.adaptor.controller.dto.request.CityVisitCreateReqDto;
import kko.traveldiary_api.journey.application.provided.CityVisitManager;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.shared.Coordinate;
import kko.traveldiary_api.shared.security.AccessMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CityVisitController {
    private final CityVisitManager cityVisitManager;

    // 여행 중 도시 방문 이력 등록 요청
    public ResponseEntity<Void> handleRegistering(@AccessMemberId Long memberId, @RequestBody CityVisitCreateReqDto reqDto) {
        CityVisit visited = cityVisitManager.visit(memberId, reqDto.journeyId(), reqDto.cityName(), reqDto.cityId(),
                new Coordinate(reqDto.latitude(), reqDto.longitude()),
                reqDto.startDate(), reqDto.endDate());
        return null;
    }

    // 도시 방문 수정 (방문-종료 일자 / 방문순서)

    // 도시 방문 이력 삭제
}
