package kko.traveldiary_api.city.application.provided;

import kko.traveldiary_api.city.domain.CityGenerateRequest;

/**
 * PENDING 상태로 등록된 도시의 상세 정보를 채워 등록을 완료하는 인바운드 유스케이스.
 * 드라이빙 어댑터(예: 이벤트 리스너)가 이 포트를 호출한다.
 */
public interface CityDetailRegistration {
    void registerDetail(CityGenerateRequest request);
}
