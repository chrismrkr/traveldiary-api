package kko.traveldiary_api.post.adaptor.infrastructure;

import jakarta.persistence.*;
import kko.traveldiary_api.post.domain.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cityVisitId;

    @Embedded
    private PlacePointEmbeddable placePoint;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    // 매퍼에서 도메인 → 엔티티 전환 시 사용
    public PostEntity(Long id, Long cityVisitId, PlacePointEmbeddable placePoint,
                      String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.cityVisitId = cityVisitId;
        this.placePoint = placePoint;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostEntity from(Post post) {
        return new PostEntity(
                post.getId(),
                post.getCityVisitId(),
                PlacePointEmbeddable.from(post.getPlacePoint()),
                post.getContent(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }

    public Post toDomain() {
        return Post.create(this.id, this.cityVisitId, this.placePoint.toDomain(),
                this.content, this.createdAt, this.modifiedAt);
    }
}
