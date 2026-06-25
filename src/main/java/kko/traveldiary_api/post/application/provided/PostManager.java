package kko.traveldiary_api.post.application.provided;

import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;

public interface PostManager {
    Post attach(Long cityVisitId, PlacePoint placePoint, String content);
    Post updateContent(Long postId, String newContent);
    void detach(Long postId);
}
