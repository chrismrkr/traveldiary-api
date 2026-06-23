package kko.traveldiary_api.post.application.required;


import kko.traveldiary_api.post.domain.Post;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Long id);
    List<Post> findByCityVisitId(Long cityVisitId);
}
