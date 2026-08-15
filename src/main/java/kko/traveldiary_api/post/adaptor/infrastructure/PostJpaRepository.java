package kko.traveldiary_api.post.adaptor.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByCityVisitId(Long cityVisitId);
    void deleteByCityVisitId(Long cityVisitId);

}
