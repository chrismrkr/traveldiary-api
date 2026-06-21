package kko.traveldiary_api.city.adaptor.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class SpringEventListenerThreadConfig {

    @Bean("cityRegisterEventPublishExecutor")
    public Executor cityRegisterEventPublishExecutor() {
        // TODO Blocking I/O로 인한 성능 이슈 - 생성형 API Rate Limit Trade-off 고려
        return Executors.newVirtualThreadPerTaskExecutor();
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(4);          // 평상시 유지 스레드 수
//        executor.setMaxPoolSize(8);           // 최대 스레드 수
//        executor.setQueueCapacity(50);        // 대기 큐 크기
//        executor.setThreadNamePrefix("genai-"); // 스레드 이름 (로그 추적용)
//        executor.setKeepAliveSeconds(60);     // 유휴 스레드 회수 시간
//        executor.setRejectedExecutionHandler(
//                new ThreadPoolExecutor.CallerRunsPolicy()); // 포화 시 정책
//        executor.setWaitForTasksToCompleteOnShutdown(true); // 종료 시 작업 완료 대기
//        executor.setAwaitTerminationSeconds(30);
//        executor.initialize();
//        return executor;
    }
}
