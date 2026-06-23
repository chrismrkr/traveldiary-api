package kko.traveldiary_api.post.application.provided;

import kko.traveldiary_api.post.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostFinder {
    Optional<Post> search(Long postId);
    List<Post> searchByCityVisitId(Long cityVisitId);
}
