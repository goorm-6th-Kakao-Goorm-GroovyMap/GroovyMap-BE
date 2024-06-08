package aespa.groovymap.place.practice.controller;

import aespa.groovymap.place.practice.dto.PracticePlacePostDto;
import aespa.groovymap.place.practice.dto.PracticePlacePostsDto;
import aespa.groovymap.place.practice.service.PracticePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PracticePlaceController {

    private final PracticePlaceService practicePlaceService;

    // 전체 연습 장소 목록 요청 받는 메서드
    @GetMapping("/practiceplace")
    public ResponseEntity getPracticePlacePosts() {
        PracticePlacePostsDto practicePlacePostsDto = practicePlaceService.getPracticePlacePosts();
        return ResponseEntity.ok(practicePlacePostsDto);
    }

    // 연습 장소 게시글 저장 요청 받는 메서드
    @PostMapping("/practiceplace")
    public ResponseEntity savePracticePlacePost(@RequestBody PracticePlacePostDto practicePlacePostDto) {
        Long practicePlacePostId = practicePlaceService.savePracticePlacePost(practicePlacePostDto);
        return ResponseEntity.ok(practicePlacePostId);
    }

    // 연습 장소 게시글 하나 요청 받는 메서드
    @GetMapping("/practiceplace/{postId}")
    public ResponseEntity getPracticePlacePost(@PathVariable("postId") Long postId) {
        PracticePlacePostDto practicePlacePostDto = practicePlaceService.getPracticePlacePost(postId);
        return ResponseEntity.ok(practicePlacePostDto);
    }

}
