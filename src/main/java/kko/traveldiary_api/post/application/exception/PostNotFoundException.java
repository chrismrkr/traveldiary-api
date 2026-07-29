package kko.traveldiary_api.post.application.exception;

public class PostNotFoundException extends IllegalArgumentException {
    private final Long postId;
    public PostNotFoundException(Long postId) {
        super("Post Not Found: " + postId);
        this.postId = postId;
    }
}
