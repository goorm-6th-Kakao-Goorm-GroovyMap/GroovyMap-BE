package aespa.groovymap.place.practice.controller;

import aespa.groovymap.place.practice.dto.PracticePlacePostsDto;
import aespa.groovymap.place.practice.service.PracticePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PracticePlaceController {

    private final PracticePlaceService practicePlaceService;

    @GetMapping("/practiceplace")
    public ResponseEntity getPracticePlacePosts() {
        PracticePlacePostsDto practicePlacePostsDto = practicePlaceService.getPracticePlacePosts();
        return ResponseEntity.ok(null);
    }
}
