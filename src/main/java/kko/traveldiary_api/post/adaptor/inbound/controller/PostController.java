package kko.traveldiary_api.post.adaptor.inbound.controller;

import kko.traveldiary_api.post.adaptor.inbound.controller.dto.request.PostAttachReqDto;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.request.PostContentsModifyDto;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.response.CommonPostResponse;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.response.PostResponseDto;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.response.PostResponseStatuses;
import kko.traveldiary_api.post.application.provided.PostFinder;
import kko.traveldiary_api.post.application.provided.PostManager;
import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;
import kko.traveldiary_api.shared.Coordinate;
import kko.traveldiary_api.shared.security.AccessMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostFinder postFinder;
    private final PostManager postManager;

    @GetMapping("/api/post")
    public ResponseEntity<CommonPostResponse<Object>> handleFindPostListReq(@AccessMemberId Long memberId, @RequestParam("cityVisitId") Long cityVisitId) {
        List<Post> posts = postFinder.searchByCityVisitId(memberId, cityVisitId);
        return ResponseEntity.ok(new CommonPostResponse<>(
                PostResponseStatuses.SUCCESS,
                posts.stream().map(PostResponseDto::convert).toList()
        ));
    }

    @GetMapping("/api/post/{postId}")
    public ResponseEntity<CommonPostResponse<Object>> handleFindPostReq(@AccessMemberId Long memberId, @PathVariable("postId") Long postId) {
        Post post = postFinder.search(memberId, postId);
        return ResponseEntity.ok(new CommonPostResponse<>(
                PostResponseStatuses.SUCCESS, PostResponseDto.convert(post)
        ));
    }

    @PostMapping("/api/post")
    public ResponseEntity<CommonPostResponse<Object>> handleAttachPostReq(@AccessMemberId Long memberId, @RequestBody PostAttachReqDto reqDto) {
        // latitude/longitude 는 빈 값으로 들어올 수 있으므로 둘 다 있을 때만 좌표를 만든다.
        Coordinate coordinate = (reqDto.latitude() != null && reqDto.longitude() != null)
                ? new Coordinate(reqDto.latitude(), reqDto.longitude())
                : null;
        Post attached = postManager.attach(memberId, reqDto.cityVisitId(),
                PlacePoint.create(reqDto.placeName(), reqDto.provider(), reqDto.placeId(), coordinate), reqDto.contents());
        return ResponseEntity.ok(new CommonPostResponse<>(
                PostResponseStatuses.SUCCESS, PostResponseDto.convert(attached)
        ));
    }

    @PatchMapping("/api/post")
    public ResponseEntity<CommonPostResponse<Object>> handleModifyContentReq(@AccessMemberId Long memberId, @RequestBody PostContentsModifyDto reqDto) {
        Post modified = postManager.updateContent(memberId, reqDto.postId(), reqDto.contents());
        return ResponseEntity.ok(new CommonPostResponse<>(
                PostResponseStatuses.SUCCESS, PostResponseDto.convert(modified)
        ));
    }

    @DeleteMapping("/api/post/{postId}")
    public ResponseEntity<Void> handleDeleteReq(@AccessMemberId Long memberId, @PathVariable("postId") Long postId) {
        postManager.detach(memberId, postId);
        return ResponseEntity.noContent().build();
    }
}
