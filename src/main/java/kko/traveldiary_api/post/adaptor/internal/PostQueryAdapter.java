package kko.traveldiary_api.post.adaptor.internal;

import kko.traveldiary_api.journey.application.required.PostQueryPort;
import kko.traveldiary_api.post.application.required.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostQueryAdapter implements PostQueryPort {
    private final PostRepository postRepository;

    @Override
    public boolean detachByCityVisitId(Long cityVisitId) {
        try {
            postRepository.deleteByCityVisitId(cityVisitId);
            return true;
        } catch (Exception e) {
            String errorMsg = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString)
                    .collect(Collectors.joining());
            log.error("[Delete Posts by CityVisitId Failed] {}", errorMsg);
            return false;
        }
    }
}
