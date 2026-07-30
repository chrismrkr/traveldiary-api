package kko.traveldiary_api.post.adaptor.inbound.controller.dto.response;

import kko.traveldiary_api.post.domain.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponseDto {
    private Long postId;
    private Long cityVisitId;
    private PlacePointResponseDto placePoint;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    private PostResponseDto(Long postId, Long cityVisitId, PlacePointResponseDto placePoint, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.postId = postId;
        this.cityVisitId = cityVisitId;
        this.placePoint = placePoint;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostResponseDto convert(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .cityVisitId(post.getCityVisitId())
                .placePoint(PlacePointResponseDto.convert(post.getPlacePoint()))
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }
}
