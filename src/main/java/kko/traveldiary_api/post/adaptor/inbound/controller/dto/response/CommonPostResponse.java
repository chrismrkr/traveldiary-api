package kko.traveldiary_api.post.adaptor.inbound.controller.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommonPostResponse<T> {
    private PostResponseStatuses status;
    private T data;
}
