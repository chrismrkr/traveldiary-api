package kko.traveldiary_api.post.application;

import kko.traveldiary_api.post.application.exception.PostAccessDeniedException;
import kko.traveldiary_api.post.application.exception.PostCityVisitNotFoundException;
import kko.traveldiary_api.post.application.exception.PostNotFoundException;
import kko.traveldiary_api.post.application.provided.PostFinder;
import kko.traveldiary_api.post.application.provided.PostManager;
import kko.traveldiary_api.post.application.required.CityVisitAccessPort;
import kko.traveldiary_api.post.application.required.PostRepository;
import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService implements PostFinder, PostManager {
    private final PostRepository postRepository;
    private final CityVisitAccessPort cityVisitAccessPort;

    @Override
    public Post search(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        validateOwnerOfPost(memberId, post);
        return post;
    }

    @Override
    public List<Post> searchByCityVisitId(Long memberId, Long cityVisitId) {
        validateOwnerOfCityVisit(memberId, cityVisitId);
        return postRepository.findByCityVisitId(cityVisitId);
    }

    @Override
    public Post attach(Long memberId, Long cityVisitId, PlacePoint placePoint, String content) {
        validateOwnerOfCityVisit(memberId, cityVisitId);
        Post post = Post.create(cityVisitId, placePoint, content);
        post = postRepository.save(post);
        return post;
    }

    @Override
    public Post updateContent(Long memberId, Long postId, String newContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        validateOwnerOfPost(memberId, post);
        post.modifyContent(newContent);
        return postRepository.save(post);
    }

    @Override
    public void detach(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        validateOwnerOfPost(memberId, post);
        postRepository.delete(postId);
    }

    @Override
    public void detachByCityVisitId(Long memberId, Long cityVisitId) {
        validateOwnerOfCityVisit(memberId, cityVisitId);
        postRepository.deleteByCityVisitId(cityVisitId);
    }

    private void validateOwnerOfCityVisit(Long memberId, Long cityVisitId) {
        if(!Objects.equals(cityVisitAccessPort.findOwnerIdOfCityVisit(cityVisitId)
                .orElseThrow(() -> new PostCityVisitNotFoundException(cityVisitId)), memberId)) {
            throw new PostAccessDeniedException(memberId);
        }
    }

    private void validateOwnerOfPost(Long memberId, Post post) {
        Long cityVisitId = post.getCityVisitId();
        validateOwnerOfCityVisit(memberId, cityVisitId);
    }
}
