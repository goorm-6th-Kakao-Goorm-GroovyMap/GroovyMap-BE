package aespa.groovymap.place.performance.controller;

import aespa.groovymap.place.performance.dto.PerformancePlacePostIdDto;
import aespa.groovymap.place.performance.dto.PerformancePlacePostRequestDto;
import aespa.groovymap.place.performance.dto.PerformancePlacePostResponseDto;
import aespa.groovymap.place.performance.dto.PerformancePlacePostsDto;
import aespa.groovymap.place.performance.service.PerformancePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PerformancePlaceController {

    private final PerformancePlaceService performancePlaceService;

    /*
        컨트롤러가 하는 일 : 요청을 받고 응답을 보내는 일
        응답으로 보내야 하는 것 : 공연 장소 목록
        공연 장소 목록 : performance place list
        List<PerformancePlacePost>
    */

    // 전체 공연 장소 목록 요청 받는 메서드
    @GetMapping("/performanceplace")
    public ResponseEntity getPerformancePlacePosts() {
        /*
            프론트엔드에서 전체 공연 장소 목록을 요청할 때 백엔드에게 넘겨야 할 것이 있는가? = 없다

            이제 로직 구현
            컨트롤러 =
              1. 요청을 받아서 서비스에게 전달하는 일,
              2. 서비스한테서 값을 받아서 다시 프론트엔드 반환 일

            서비스야 이런 요청이 왔는데 메서드 호출할테니 이런 값을 줘
            서비스의 전체 공연 장소 목록을 반환하는 메서드를 호출해서 값을 달라고 하는 것
        */

        PerformancePlacePostsDto performancePlacePostsDto = performancePlaceService.getPerformancePlacePosts();

        return ResponseEntity.ok(performancePlacePostsDto);
    }

    // 공연 장소 게시글 저장 요청 받는 메서드
    @PostMapping("/performanceplace")
    public ResponseEntity savePerformancePlacePost(
            @RequestBody PerformancePlacePostRequestDto performancePlacePostRequestDto) {
        Long performancePlacePostId = performancePlaceService.savePerformancePlacePost(performancePlacePostRequestDto);
        PerformancePlacePostIdDto performancePlacePostIdDto = new PerformancePlacePostIdDto();
        performancePlacePostIdDto.setId(performancePlacePostId);
        return ResponseEntity.ok(performancePlacePostIdDto);
    }

    // 공연 장소 게시글 하나 요청 받는 메서드
    @GetMapping("/performanceplace/{postId}")
    public ResponseEntity getPerformancePlacePost(@PathVariable("postId") Long postId) {
        PerformancePlacePostResponseDto performancePlacePostResponseDto = performancePlaceService.getPerformancePlacePost(
                postId);
        return ResponseEntity.ok(performancePlacePostResponseDto);
    }


}
