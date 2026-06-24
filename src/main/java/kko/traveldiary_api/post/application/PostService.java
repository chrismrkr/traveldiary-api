package kko.traveldiary_api.post.application;

import kko.traveldiary_api.post.application.provided.PostFinder;
import kko.traveldiary_api.post.application.provided.PostManager;
import kko.traveldiary_api.post.application.required.PostRepository;
import kko.traveldiary_api.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService implements PostFinder, PostManager {
    private final PostRepository postRepository;

    @Override
    public Optional<Post> search(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public List<Post> searchByCityVisitId(Long cityVisitId) {
        return null;
    }

    @Override
    public Post attach(Post post) {
        return null;
    }

    @Override
    public Post updateContent(Long postId, String newContent) {
        return null;
    }

    @Override
    public void detach(Post post) {

    }
}
