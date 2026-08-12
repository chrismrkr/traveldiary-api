package kko.traveldiary_api.post.adaptor.infrastructure;

import jakarta.persistence.*;
import kko.traveldiary_api.post.domain.PlacePoint;
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
                post.getPlacePoint() == null ? null : PlacePointEmbeddable.from(post.getPlacePoint()),
                post.getContent(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }

    public Post toDomain() {
        // PlacePoint 의 모든 필드가 선택값이므로, 컬럼이 전부 null 이면 Hibernate 가 embeddable 자체를 null 로 매핑한다.
        // 이때 빈 PlacePoint 로 복원해야 등록 응답과 조회 응답의 placePoint 형태가 같아진다.
        PlacePoint placePoint = this.placePoint == null ? PlacePoint.empty() : this.placePoint.toDomain();
        return Post.create(this.id, this.cityVisitId, placePoint,
                this.content, this.createdAt, this.modifiedAt);
    }
}
