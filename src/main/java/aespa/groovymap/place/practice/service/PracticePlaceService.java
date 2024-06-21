package aespa.groovymap.place.practice.service;

import aespa.groovymap.domain.Place;
import aespa.groovymap.domain.post.PracticePlacePost;
import aespa.groovymap.place.practice.dto.PracticePlacePostRequestDto;
import aespa.groovymap.place.practice.dto.PracticePlacePostResponseDto;
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
        List<PracticePlacePostResponseDto> practicePlacePostResponseDtos = new ArrayList<>();

        for (PracticePlacePost practicePlacePost : practicePlacePosts) {
            PracticePlacePostResponseDto practicePlacePostResponseDto = convertToPracticePlacePostDto(
                    practicePlacePost);
            practicePlacePostResponseDtos.add(practicePlacePostResponseDto);
        }

        PracticePlacePostsDto practicePlacePostsDto = new PracticePlacePostsDto();
        practicePlacePostsDto.setPracticePlacePosts(practicePlacePostResponseDtos);
        return practicePlacePostsDto;
    }

    // 연습 장소 저장
    public Long savePracticePlacePost(PracticePlacePostRequestDto practicePlacePostRequestDto) {
        PracticePlacePost practicePlacePost = new PracticePlacePost();

        practicePlacePost.setCategory(practicePlacePostRequestDto.getPart());
        practicePlacePost.setCoordinate(practicePlacePostRequestDto.getCoordinate());
        practicePlacePost.setPlace(convertToPlace(practicePlacePostRequestDto));

        return practicePlaceRepository.save(practicePlacePost).getId();
    }

    // Place 객체 PracticePlaceDto로부터 생성
    public Place convertToPlace(PracticePlacePostRequestDto practicePlacePostRequestDto) {
        Place place = new Place();

        place.setName(practicePlacePostRequestDto.getName());
        place.setRegion(practicePlacePostRequestDto.getRegion());
        place.setAddress(practicePlacePostRequestDto.getAddress());
        place.setPhoneNumber(practicePlacePostRequestDto.getPhoneNumber());
        place.setRentalFee(practicePlacePostRequestDto.getRentalFee());
        place.setCapacity(practicePlacePostRequestDto.getCapacity());
        place.setHours(practicePlacePostRequestDto.getPracticeHours());
        place.setDescription(practicePlacePostRequestDto.getDescription());

        return place;
    }

    // id로 PracticePlacePost 찾기
    public PracticePlacePostResponseDto getPracticePlacePost(Long postId) {
        PracticePlacePost practicePlacePost = practicePlaceRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        return convertToPracticePlacePostDto(practicePlacePost);
    }


    // PracticePlacePost -> PracticePlacePostDto 변환
    public PracticePlacePostResponseDto convertToPracticePlacePostDto(PracticePlacePost practicePlacePost) {
        PracticePlacePostResponseDto practicePlacePostResponseDto = new PracticePlacePostResponseDto();

        practicePlacePostResponseDto.setId(practicePlacePost.getId());
        practicePlacePostResponseDto.setPart(practicePlacePost.getCategory());
        practicePlacePostResponseDto.setCoordinate(practicePlacePost.getCoordinate());

        Place place = practicePlacePost.getPlace();

        practicePlacePostResponseDto.setName(place.getName());
        practicePlacePostResponseDto.setRegion(place.getRegion());
        practicePlacePostResponseDto.setAddress(place.getAddress());
        practicePlacePostResponseDto.setPhoneNumber(place.getPhoneNumber());
        practicePlacePostResponseDto.setRentalFee(place.getRentalFee());
        practicePlacePostResponseDto.setCapacity(place.getCapacity());
        practicePlacePostResponseDto.setPracticeHours(place.getHours());
        practicePlacePostResponseDto.setDescription(place.getDescription());

        return practicePlacePostResponseDto;
    }
}
