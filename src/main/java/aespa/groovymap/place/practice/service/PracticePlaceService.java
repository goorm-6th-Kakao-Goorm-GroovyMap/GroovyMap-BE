package aespa.groovymap.place.practice.service;

import aespa.groovymap.domain.Place;
import aespa.groovymap.domain.post.PracticePlacePost;
import aespa.groovymap.place.practice.dto.PracticePlacePostDto;
import aespa.groovymap.place.practice.dto.PracticePlacePostsDto;
import aespa.groovymap.place.practice.repository.PracticePlaceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PracticePlaceService {

    private final PracticePlaceRepository practicePlaceRepository;

    // 전체 연습 장소 목록 반환
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

    // 연습 장소 저장
    public Long savePracticePlacePost(PracticePlacePostDto practicePlacePostDto) {
        PracticePlacePost practicePlacePost = new PracticePlacePost();

        practicePlacePost.setCategory(practicePlacePostDto.getPart());
        practicePlacePost.setCoordinate(practicePlacePostDto.getCoordinate());
        practicePlacePost.setPlace(convertToPlace(practicePlacePostDto));

        return practicePlaceRepository.save(practicePlacePost).getId();
    }

    // Place 객체 PracticePlaceDto로부터 생성
    public Place convertToPlace(PracticePlacePostDto practicePlacePostDto) {
        Place place = new Place();

        place.setName(practicePlacePostDto.getName());
        place.setRegion(practicePlacePostDto.getRegion());
        place.setAddress(practicePlacePostDto.getAddress());
        place.setPhoneNumber(practicePlacePostDto.getPhoneNumber());
        place.setRentalFee(practicePlacePostDto.getRentalFee());
        place.setCapacity(practicePlacePostDto.getCapacity());
        place.setHours(practicePlacePostDto.getPracticeHours());
        place.setDescription(practicePlacePostDto.getDescription());

        return place;
    }

    // id로 PracticePlacePost 찾기
    public PracticePlacePostDto getPracticePlacePost(Long postId) {
        PracticePlacePost practicePlacePost = practicePlaceRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        return convertToPracticePlacePostDto(practicePlacePost);
    }


    // PracticePlacePost -> PracticePlacePostDto 변환
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
