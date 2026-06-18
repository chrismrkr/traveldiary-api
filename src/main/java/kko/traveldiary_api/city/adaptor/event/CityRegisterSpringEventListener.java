package kko.traveldiary_api.city.adaptor.event;

import kko.traveldiary_api.city.application.provided.CityDetailRegistration;
import kko.traveldiary_api.city.domain.CityGenerateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 스프링 이벤트를 수신하는 드라이빙(인바운드) 어댑터.
 * 이벤트를 받아 인바운드 포트(CityDetailsRegistration)를 호출한다.
 */
@Component
@RequiredArgsConstructor
public class CityRegisterSpringEventListener {
    private final CityDetailRegistration cityDetailRegistration;

    @Async
    @EventListener
    public void handle(CityGenerateRequest request) {
        cityDetailRegistration.registerDetail(request);
    }
}
