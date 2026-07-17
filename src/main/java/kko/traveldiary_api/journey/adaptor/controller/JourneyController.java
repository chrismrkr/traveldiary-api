package kko.traveldiary_api.journey.adaptor.controller;

import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyRegisterReqDto;
import kko.traveldiary_api.journey.adaptor.controller.dto.response.CommonResponse;
import kko.traveldiary_api.journey.adaptor.controller.dto.response.JourneyResponseDto;
import kko.traveldiary_api.journey.adaptor.controller.dto.response.ResponseStatuses;
import kko.traveldiary_api.journey.application.provided.JourneyFinder;
import kko.traveldiary_api.journey.application.provided.JourneyManager;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.shared.security.AccessMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JourneyController {
    private final JourneyManager journeyManager;
    private final JourneyFinder journeyFinder;

    @GetMapping("/api/journey/{journeyId}")
    public ResponseEntity<CommonResponse<Object>> handleFineJourneyByJourneyIdRequest(@AccessMemberId Long memberId, @PathVariable("journeyId") Long journeyId) {
        if(journeyId != null) {
            Journey journey = journeyFinder.findJourney(memberId, journeyId);
            return ResponseEntity.ok(
                    new CommonResponse<>(ResponseStatuses.SUCCESS,
                            JourneyResponseDto.convert(journey))
            );
        }
        List<Journey> myJourneys = journeyFinder.findMyJourneys(memberId);
        return ResponseEntity.ok(
                new CommonResponse<>(ResponseStatuses.SUCCESS,
                        myJourneys.stream().map(JourneyResponseDto::convert).toList()
                )
        );
    }

    @PostMapping("/api/journey")
    public ResponseEntity<CommonResponse<JourneyResponseDto>> handleRegisteringJourneyRequest(@RequestBody JourneyRegisterReqDto reqDto) {
        Journey registered = journeyManager.register(reqDto);
        return ResponseEntity.ok(new CommonResponse<>(
           ResponseStatuses.SUCCESS, JourneyResponseDto.convert(registered)
        ));
    }

    @PatchMapping("/api/journey")
    public ResponseEntity<CommonResponse<JourneyResponseDto>> handlePatchingJourneyRequest(@AccessMemberId Long memberId, @RequestBody JourneyPatchReqDto reqDto) {
        Journey modified = journeyManager.modify(memberId, reqDto);
        return ResponseEntity.ok(new CommonResponse<>(
                ResponseStatuses.SUCCESS, JourneyResponseDto.convert(modified)
        ));
    }

    @DeleteMapping("/api/journey/{journeyId}")
    public ResponseEntity<Void> handleDeletingJourneyRequest(@AccessMemberId Long memberId, @PathVariable("journeyId") Long journeyId) {
        journeyManager.delete(memberId, journeyId);
        return ResponseEntity.noContent().build();
    }
}
