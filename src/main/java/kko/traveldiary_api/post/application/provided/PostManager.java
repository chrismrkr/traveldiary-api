package kko.traveldiary_api.post.application.provided;

import kko.traveldiary_api.post.domain.Post;

public interface PostManager {
    Post attach(Post post);
    Post updateContent(Long postId, String newContent);
    void detach(Post post);
}
