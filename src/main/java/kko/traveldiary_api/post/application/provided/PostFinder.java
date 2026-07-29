package kko.traveldiary_api.post.application.provided;

import kko.traveldiary_api.post.domain.Post;

import java.util.List;


public interface PostFinder {
    Post search(Long memberId, Long postId);
    List<Post> searchByCityVisitId(Long memberId, Long cityVisitId);
}
