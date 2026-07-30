package kko.traveldiary_api.journey.adaptor.inbound.controller;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitCreateReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitModifyReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitOrderRealignReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.CityVisitResponseDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.CommonJourneyResponse;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.JourneyResponseStatuses;
import kko.traveldiary_api.journey.application.provided.CityVisitManager;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.shared.Coordinate;
import kko.traveldiary_api.shared.security.AccessMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CityVisitController {
    private final CityVisitManager cityVisitManager;

    // 여행 중 도시 방문 이력 등록
    @PostMapping("/api/city-visit")
    public ResponseEntity<CommonJourneyResponse<CityVisitResponseDto>> handleCityVisitRegister(
            @AccessMemberId Long memberId, @RequestBody CityVisitCreateReqDto reqDto) {
        CityVisit visited = cityVisitManager.visit(memberId, reqDto.journeyId(), reqDto.cityName(), reqDto.cityId(),
                new Coordinate(reqDto.latitude(), reqDto.longitude()),
                reqDto.startDate(), reqDto.endDate());
        return ResponseEntity.ok(new CommonJourneyResponse<>(
                JourneyResponseStatuses.SUCCESS, CityVisitResponseDto.convert(visited)));
    }

    // 도시 방문 기간(시작-종료 일자) 수정
    @PatchMapping("/api/city-visit")
    public ResponseEntity<CommonJourneyResponse<CityVisitResponseDto>> handleCityVisitModify(
            @AccessMemberId Long memberId, @RequestBody CityVisitModifyReqDto reqDto) {
        CityVisit modified = cityVisitManager.modify(memberId, reqDto);
        return ResponseEntity.ok(new CommonJourneyResponse<>(
                JourneyResponseStatuses.SUCCESS, CityVisitResponseDto.convert(modified)));
    }

    // 도시 방문 순서 재정렬
    @PatchMapping("/api/city-visit/order")
    public ResponseEntity<CommonJourneyResponse<Object>> handleCityVisitOrderRealign(
            @AccessMemberId Long memberId, @RequestBody CityVisitOrderRealignReqDto reqDto) {
        cityVisitManager.realignVisitOrder(memberId, reqDto);
        return ResponseEntity.ok(new CommonJourneyResponse<>(JourneyResponseStatuses.SUCCESS, null));
    }

    // 도시 방문 이력 삭제
    @DeleteMapping("/api/city-visit/{cityVisitId}")
    public ResponseEntity<Void> handleCityVisitDelete(
            @AccessMemberId Long memberId, @PathVariable("cityVisitId") Long cityVisitId,
            @RequestParam("journeyId") Long journeyId) {
        cityVisitManager.delete(memberId, journeyId, cityVisitId);
        return ResponseEntity.noContent().build();
    }
}
