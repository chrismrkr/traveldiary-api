package kko.traveldiary_api.city.domain;

import kko.traveldiary_api.shared.Coordinate;

/**
 * 해당 좌표의 City 가 아직 존재하지 않아 비동기 생성을 트리거한 상태.
 * "잘못된 요청"이 아니라 "아직 준비되지 않음"을 의미하므로,
 * 컨트롤러에서는 HTTP 202(Accepted) 또는 404 로 매핑하여 프론트가 폴링하도록 한다.
 */
public class CityNotReadyException extends RuntimeException {
    public CityNotReadyException(City city) {
        super("City is being generated for coordinate: ("
                + city.getCoordinate().getLatitude() + ", " + city.getCoordinate().getLongitude() + ")");
    }

    public CityNotReadyException(Coordinate coordinate) {
        super("City is being generated for coordinate: ("
                + coordinate.getLatitude() + ", " + coordinate.getLongitude() + ")");
    }
}
