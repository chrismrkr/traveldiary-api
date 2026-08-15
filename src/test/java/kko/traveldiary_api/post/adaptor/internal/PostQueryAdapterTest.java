package kko.traveldiary_api.post.adaptor.internal;

import kko.traveldiary_api.post.application.required.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

/**
 * journey 모듈이 의존하는 포트의 계약: 삭제 실패를 예외로 던지지 않고 false 로 알린다.
 * (호출자인 JourneyService/CityVisitService 가 false 를 보고 자신의 삭제를 중단한다)
 */
class PostQueryAdapterTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final PostQueryAdapter adapter = new PostQueryAdapter(postRepository);

    @Test
    @DisplayName("삭제에 성공하면 true 를 반환한다")
    void detachByCityVisitId_success() {
        assertThat(adapter.detachByCityVisitId(10L)).isTrue();

        then(postRepository).should().deleteByCityVisitId(10L);
    }

    @Test
    @DisplayName("삭제 중 예외가 나면 전파하지 않고 false 를 반환한다")
    void detachByCityVisitId_failure() {
        willThrow(new RuntimeException("DB down")).given(postRepository).deleteByCityVisitId(10L);

        assertThatCode(() -> assertThat(adapter.detachByCityVisitId(10L)).isFalse())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("지울 Post 가 없어도 true 를 반환한다")
    void detachByCityVisitId_noPosts() {
        // 파생 삭제 쿼리는 대상이 없어도 예외 없이 끝난다.
        assertThat(adapter.detachByCityVisitId(999L)).isTrue();
    }
}
