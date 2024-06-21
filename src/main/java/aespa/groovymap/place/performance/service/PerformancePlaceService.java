package aespa.groovymap.place.performance.service;

import aespa.groovymap.domain.Place;
import aespa.groovymap.domain.post.PerformancePlacePost;
import aespa.groovymap.place.performance.dto.PerformancePlacePostRequestDto;
import aespa.groovymap.place.performance.dto.PerformancePlacePostResponseDto;
import aespa.groovymap.place.performance.dto.PerformancePlacePostsDto;
import aespa.groovymap.place.performance.repository.PerformancePlaceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformancePlaceService {

    private final PerformancePlaceRepository performancePlaceRepository;

    /*
        서비스가 하는 일 : 컨트롤러의 요청을 받아서 비즈니스 로직을 수행하는 것
        비즈니스 로직 : 프로그램에서 동작하는 로직

        프론트엔드 - 컨트롤러 - 서비스   -  리포지토리 (DB)
        요청      - 요청받음 - 로직수행 - DB에서 필요한 값 제공

        여기서 수행하는 비즈니스 로직 : 전체 공연 장소 목록 반환
        전체 공연 장소 목록이 DB에 저장되어 있음
        리포지토리에게 전체 공연 장소 목록 달라고 하면 됨
    */

    // 전체 공연 장소 목록 반환
    public PerformancePlacePostsDto getPerformancePlacePosts() {
        List<PerformancePlacePost> performancePlacePosts = performancePlaceRepository.findAll();
        List<PerformancePlacePostResponseDto> performancePlacePostResponseDtos = new ArrayList<>();

        for (PerformancePlacePost performancePlacePost : performancePlacePosts) {
            PerformancePlacePostResponseDto performancePlacePostResponseDto = convertToPerformancePlacePostDto(
                    performancePlacePost);
            performancePlacePostResponseDtos.add(performancePlacePostResponseDto);
        }

        PerformancePlacePostsDto performancePlacePostsDto = new PerformancePlacePostsDto();
        performancePlacePostsDto.setPerformancePlacePosts(performancePlacePostResponseDtos);
        return performancePlacePostsDto;
    }

    // 공연 장소 저장
    public Long savePerformancePlacePost(PerformancePlacePostRequestDto performancePlacePostRequestDto) {
        PerformancePlacePost performancePlacePost = new PerformancePlacePost();

        performancePlacePost.setCategory(performancePlacePostRequestDto.getPart());
        performancePlacePost.setCoordinate(performancePlacePostRequestDto.getCoordinate());
        performancePlacePost.setPlace(convertToPlace(performancePlacePostRequestDto));

        return performancePlaceRepository.save(performancePlacePost).getId();
    }

    // Place 객체 PerformancePlaceDto로부터 생성
    public Place convertToPlace(PerformancePlacePostRequestDto performancePlacePostRequestDto) {
        Place place = new Place();

        place.setName(performancePlacePostRequestDto.getName());
        place.setRegion(performancePlacePostRequestDto.getRegion());
        place.setAddress(performancePlacePostRequestDto.getAddress());
        place.setPhoneNumber(performancePlacePostRequestDto.getPhoneNumber());
        place.setRentalFee(performancePlacePostRequestDto.getRentalFee());
        place.setCapacity(performancePlacePostRequestDto.getCapacity());
        place.setHours(performancePlacePostRequestDto.getPerformanceHours());
        place.setDescription(performancePlacePostRequestDto.getDescription());

        return place;
    }

    // id로 PerformancePlacePost 찾는 메서드
    public PerformancePlacePostResponseDto getPerformancePlacePost(Long postId) {
        PerformancePlacePost performancePlacePost = performancePlaceRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        return convertToPerformancePlacePostDto(performancePlacePost);
    }

    // PerformancePlacePost -> PerformancePlacePostDto 변환 메서드
    private PerformancePlacePostResponseDto convertToPerformancePlacePostDto(
            PerformancePlacePost performancePlacePost) {
        PerformancePlacePostResponseDto performancePlacePostResponseDto = new PerformancePlacePostResponseDto();

        performancePlacePostResponseDto.setId(performancePlacePost.getId());
        performancePlacePostResponseDto.setCoordinate(performancePlacePost.getCoordinate());
        performancePlacePostResponseDto.setPart(performancePlacePost.getCategory());

        Place place = performancePlacePost.getPlace();

        performancePlacePostResponseDto.setName(place.getName());
        performancePlacePostResponseDto.setRegion(place.getRegion());
        performancePlacePostResponseDto.setAddress(place.getAddress());
        performancePlacePostResponseDto.setPhoneNumber(place.getPhoneNumber());
        performancePlacePostResponseDto.setRentalFee(place.getRentalFee());
        performancePlacePostResponseDto.setCapacity(place.getCapacity());
        performancePlacePostResponseDto.setPerformanceHours(place.getHours());
        performancePlacePostResponseDto.setDescription(place.getDescription());

        return performancePlacePostResponseDto;
    }
}
