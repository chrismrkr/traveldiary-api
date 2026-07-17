package kko.traveldiary_api.journey.adaptor.controller;

import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyRegisterReqDto;
import kko.traveldiary_api.journey.application.provided.JourneyFinder;
import kko.traveldiary_api.journey.application.provided.JourneyManager;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.shared.security.AccessMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class JourneyController {
    private final JourneyManager journeyManager;
    private final JourneyFinder journeyFinder;

    @GetMapping("/api/journey/{journeyId}")
    public void handleFineJourneyByJourneyIdRequest(@AccessMemberId Long memberId, @PathVariable("journeyId") Long journeyId) {
        if(journeyId != null) {
            journeyFinder.findJourney(memberId, journeyId);
            return;
        }

    }

    @PostMapping("/api/journey")
    public void handleRegisteringJourneyRequest(@RequestBody JourneyRegisterReqDto reqDto) {
        Journey registered = journeyManager.register(reqDto);
    }

    @PatchMapping("/api/journey")
    public void handlePatchingJourneyRequest(@AccessMemberId Long memberId, @RequestBody JourneyPatchReqDto reqDto) {
        Journey modified = journeyManager.modify(memberId, reqDto);
    }

    @DeleteMapping("/api/journey/{journeyId}")
    public void handleDeletingJourneyRequest(@AccessMemberId Long memberId, @PathVariable("journeyId") Long journeyId) {
        journeyManager.delete(memberId, journeyId);
    }
}
