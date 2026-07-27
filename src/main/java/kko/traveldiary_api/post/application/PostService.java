package kko.traveldiary_api.post.application;

import kko.traveldiary_api.post.application.provided.PostFinder;
import kko.traveldiary_api.post.application.provided.PostManager;
import kko.traveldiary_api.post.application.required.CityVisitQueryPort;
import kko.traveldiary_api.post.application.required.PostRepository;
import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService implements PostFinder, PostManager {
    private final PostRepository postRepository;
    private final CityVisitQueryPort cityVisitQueryPort;

    @Override
    public Optional<Post> search(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public List<Post> searchByCityVisitId(Long cityVisitId) {
        return postRepository.findByCityVisitId(cityVisitId);
    }


    @Override
    public Post attach(Long cityVisitId, PlacePoint placePoint, String content) {
        Post post = Post.create(cityVisitId, placePoint, content);
        post = postRepository.save(post);
        return post;
    }

    @Override
    @Transactional
    public Post updateContent(Long postId, String newContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Post Id: Not Found"));
        post.modifyContent(newContent);
        return postRepository.save(post);
    }

    @Override
    public void detach(Long postId) {
        postRepository.delete(postId);
    }
}
