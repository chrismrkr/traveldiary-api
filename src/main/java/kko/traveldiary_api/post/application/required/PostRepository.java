package kko.traveldiary_api.post.application.required;


import kko.traveldiary_api.post.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Long id);
    List<Post> findByCityVisitId(Long cityVisitId);
    Post save(Post post);
    void delete(Long postId);
    void delete(Post post);
    void deleteByCityVisitId(Long cityVisitId);
}
