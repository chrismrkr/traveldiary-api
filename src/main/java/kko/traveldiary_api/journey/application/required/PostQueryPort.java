package kko.traveldiary_api.journey.application.required;

public interface PostQueryPort {
    /**
     * CityVisit 에 달린 Post 를 모두 제거한다.
     * 소유자 검증은 호출자(journey 모듈)가 이미 마쳤다고 보고 여기서는 다시 하지 않는다.
     *
     * @return 삭제에 성공하면 true, 실패하면 false
     */
    boolean detachByCityVisitId(Long cityVisitId);
}
