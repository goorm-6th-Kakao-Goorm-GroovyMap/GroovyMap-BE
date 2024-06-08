package aespa.groovymap.place.practice.service;

import aespa.groovymap.domain.Place;
import aespa.groovymap.domain.post.PracticePlacePost;
import aespa.groovymap.place.practice.dto.PracticePlacePostDto;
import aespa.groovymap.place.practice.dto.PracticePlacePostsDto;
import aespa.groovymap.place.practice.repository.PracticePlaceRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PracticePlaceService {

    private final PracticePlaceRepository practicePlaceRepository;

    public PracticePlacePostsDto getPracticePlacePosts() {
        List<PracticePlacePost> practicePlacePosts = practicePlaceRepository.findAll();
        List<PracticePlacePostDto> practicePlacePostDtos = new ArrayList<>();

        for (PracticePlacePost practicePlacePost : practicePlacePosts) {
            PracticePlacePostDto practicePlacePostDto = convertToPracticePlacePostDto(practicePlacePost);
            practicePlacePostDtos.add(practicePlacePostDto);
        }

        PracticePlacePostsDto practicePlacePostsDto = new PracticePlacePostsDto();
        practicePlacePostsDto.setPracticePlacePosts(practicePlacePostDtos);
        return practicePlacePostsDto;
    }

    public PracticePlacePostDto convertToPracticePlacePostDto(PracticePlacePost practicePlacePost) {
        PracticePlacePostDto practicePlacePostDto = new PracticePlacePostDto();

        practicePlacePostDto.setPart(practicePlacePost.getCategory());
        practicePlacePostDto.setCoordinate(practicePlacePost.getCoordinate());

        Place place = practicePlacePost.getPlace();

        practicePlacePostDto.setName(place.getName());
        practicePlacePostDto.setRegion(place.getRegion());
        practicePlacePostDto.setAddress(place.getAddress());
        practicePlacePostDto.setPhoneNumber(place.getPhoneNumber());
        practicePlacePostDto.setRentalFee(place.getRentalFee());
        practicePlacePostDto.setCapacity(place.getCapacity());
        practicePlacePostDto.setPracticeHours(place.getHours());
        practicePlacePostDto.setDescription(place.getDescription());

        return practicePlacePostDto;
    }
}
