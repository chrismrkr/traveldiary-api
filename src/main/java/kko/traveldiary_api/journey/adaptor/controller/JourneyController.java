package kko.traveldiary_api.journey.adaptor.controller;

import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.controller.dto.request.JourneyRegisterReqDto;
import kko.traveldiary_api.journey.application.provided.CityVisitManager;
import kko.traveldiary_api.journey.application.provided.JourneyFinder;
import kko.traveldiary_api.journey.application.provided.JourneyManager;
import kko.traveldiary_api.journey.domain.Journey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class JourneyController {
    private final JourneyManager journeyManager;
    private final JourneyFinder journeyFinder;

    @GetMapping("/api/journey")
    public void handleFineJourneyByJourneyIdRequest(@RequestParam("journeyId") String journeyId) {
        if(journeyId != null) {
            journeyFinder.findJourney(Long.parseLong(journeyId));
            return;
        }

        

    }

    @PostMapping("/api/journey")
    public void handleRegisteringJourneyRequest(@RequestBody JourneyRegisterReqDto reqDto) {
        Journey registered = journeyManager.register(reqDto);
    }

    @PatchMapping("/api/journey")
    public void handlePatchingJourneyRequest(@RequestBody JourneyPatchReqDto reqDto) {
        Journey modified = journeyManager.modify(reqDto);
    }

    @DeleteMapping("/api/journey/{journeyId}")
    public void handleDeletingJourneyRequest(@PathVariable("journeyId") String journeyId) {
        journeyManager.delete(Long.parseLong(journeyId));
    }
}
