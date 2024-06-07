package aespa.groovymap.place.practice.service;

import aespa.groovymap.place.practice.dto.PracticePlacePostsDto;
import aespa.groovymap.place.practice.repository.PracticePlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PracticePlaceService {

    private final PracticePlaceRepository practicePlaceRepository;

    public PracticePlacePostsDto getPracticePlacePosts() {
        PracticePlacePostsDto practicePlacePostsDto = new PracticePlacePostsDto();
        practicePlacePostsDto.setPracticePlacePosts(practicePlaceRepository.findAll());
        return practicePlacePostsDto;
    }
}
