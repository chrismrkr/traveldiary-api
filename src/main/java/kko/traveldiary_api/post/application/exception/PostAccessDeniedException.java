package kko.traveldiary_api.post.application.exception;

public class PostAccessDeniedException extends IllegalArgumentException {
    private final Long memberId;
    public PostAccessDeniedException(Long memberId) {
        super("Post Access Denied: Member Not Own Post");
        this.memberId = memberId;
    }
}
