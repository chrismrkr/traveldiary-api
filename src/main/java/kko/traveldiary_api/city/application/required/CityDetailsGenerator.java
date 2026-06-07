package kko.traveldiary_api.city.application.required;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.shared.Coordinate;
ㄷ
/**
 * 좌표로 도시 조회에 실패한 경우, 생성형 AI를 통해 도시 정보를 채워 City 를 생성한다.
 * 구현체(어댑터)는 infrastructure 계층에서 AI 호출을 담당한다.
 */
public interface CityDetailsGenerator {
    City generate(String name, Coordinate coordinate);
}
