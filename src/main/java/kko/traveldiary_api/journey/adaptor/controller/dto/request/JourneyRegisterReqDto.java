package kko.traveldiary_api.journey.adaptor.controller.dto.request;



import java.time.LocalDate;


public record JourneyRegisterReqDto(Long memberId,
                                    LocalDate startDate, LocalDate endDate,
                                    String name, String visibility) {

}
