package kko.traveldiary_api.post.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    private Long id;
    private Long cityVisitId;

    private PlacePoint placePoint;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Post create(Long cityVisitId, PlacePoint placePoint, String content) {
        return new Post(cityVisitId, placePoint, content);
    }

    public static Post create(Long id, Long cityVisitId, PlacePoint placePoint, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Post(id, cityVisitId, placePoint, content, createdAt, modifiedAt);
    }

    private Post(Long cityVisitId, PlacePoint placePoint, String content) {
        this.cityVisitId = cityVisitId;
        this.placePoint = placePoint;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    private Post(Long id, Long cityVisitId, PlacePoint placePoint, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.cityVisitId = cityVisitId;
        this.placePoint = placePoint;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void modifyContent(String content) {
        this.content = content;
    }
}
