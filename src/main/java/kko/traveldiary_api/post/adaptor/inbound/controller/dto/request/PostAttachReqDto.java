package kko.traveldiary_api.post.adaptor.inbound.controller.dto.request;

import java.math.BigDecimal;

public record PostAttachReqDto(
        Long cityVisitId,
        String placeName, String provider, String placeId, BigDecimal latitude, BigDecimal longitude,
        String contents
) { }
