package kko.traveldiary_api.post.adaptor.infrastructure;

import kko.traveldiary_api.post.application.required.PostRepository;
import kko.traveldiary_api.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDatabaseRepository implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository.findById(id)
                .map(PostEntity::toDomain);
    }

    @Override
    public List<Post> findByCityVisitId(Long cityVisitId) {
        return postJpaRepository.findByCityVisitId(cityVisitId)
                .stream().map(PostEntity::toDomain)
                .toList();
    }

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(PostEntity.from(post))
                .toDomain();
    }

    @Override
    public void delete(Long postId) {
        postJpaRepository.deleteById(postId);
    }

    @Override
    public void delete(Post post) {
        postJpaRepository.delete(PostEntity.from(post));
    }
}
