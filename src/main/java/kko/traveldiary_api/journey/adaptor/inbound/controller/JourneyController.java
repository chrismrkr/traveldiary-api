package kko.traveldiary_api.journey.adaptor.inbound.controller;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyRegisterReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.CommonJourneyResponse;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.JourneyResponseDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.JourneyResponseStatuses;
import kko.traveldiary_api.journey.application.provided.JourneyFinder;
import kko.traveldiary_api.journey.application.provided.JourneyManager;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.shared.security.AccessMemberId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JourneyController {
    private final JourneyManager journeyManager;
    private final JourneyFinder journeyFinder;

    @GetMapping("/api/journey")
    public ResponseEntity<CommonJourneyResponse<Object>> handleFindMyJourneysRequest(@AccessMemberId Long memberId) {
        List<Journey> myJourneys = journeyFinder.findMyJourneysWithCityVisit(memberId);
        return ResponseEntity.ok(
                new CommonJourneyResponse<>(JourneyResponseStatuses.SUCCESS,
                        myJourneys.stream().map(JourneyResponseDto::convert).toList()
                )
        );
    }

    @GetMapping("/api/journey/{journeyId}")
    public ResponseEntity<CommonJourneyResponse<Object>> handleFindJourneyByJourneyIdRequest(@AccessMemberId Long memberId, @PathVariable("journeyId") Long journeyId) {
        Journey journey = journeyFinder.findJourney(memberId, journeyId);
        return ResponseEntity.ok(
                new CommonJourneyResponse<>(JourneyResponseStatuses.SUCCESS,
                        JourneyResponseDto.convert(journey))
        );
    }

    @PostMapping("/api/journey")
    public ResponseEntity<CommonJourneyResponse<JourneyResponseDto>> handleRegisteringJourneyRequest(@AccessMemberId Long memberId, @RequestBody JourneyRegisterReqDto reqDto) {
        Journey registered = journeyManager.register(memberId, reqDto);
        return ResponseEntity.ok(new CommonJourneyResponse<>(
           JourneyResponseStatuses.SUCCESS, JourneyResponseDto.convert(registered)
        ));
    }

    @PatchMapping("/api/journey")
    public ResponseEntity<CommonJourneyResponse<JourneyResponseDto>> handlePatchingJourneyRequest(@AccessMemberId Long memberId, @RequestBody JourneyPatchReqDto reqDto) {
        Journey modified = journeyManager.modify(memberId, reqDto);
        return ResponseEntity.ok(new CommonJourneyResponse<>(
                JourneyResponseStatuses.SUCCESS, JourneyResponseDto.convert(modified)
        ));
    }

    @DeleteMapping("/api/journey/{journeyId}")
    public ResponseEntity<Void> handleDeletingJourneyRequest(@AccessMemberId Long memberId, @PathVariable("journeyId") Long journeyId) {
        journeyManager.delete(memberId, journeyId);
        return ResponseEntity.noContent().build();
    }
}
