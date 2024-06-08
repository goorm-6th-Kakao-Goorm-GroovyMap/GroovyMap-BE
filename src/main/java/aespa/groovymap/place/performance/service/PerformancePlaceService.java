package aespa.groovymap.place.performance.service;

import aespa.groovymap.domain.Place;
import aespa.groovymap.domain.post.PerformancePlacePost;
import aespa.groovymap.place.performance.dto.PerformancePlacePostDto;
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
        List<PerformancePlacePostDto> performancePlacePostDtos = new ArrayList<>();

        for (PerformancePlacePost performancePlacePost : performancePlacePosts) {
            PerformancePlacePostDto performancePlacePostDto = convertToPerformancePlacePostDto(performancePlacePost);
            performancePlacePostDtos.add(performancePlacePostDto);
        }

        PerformancePlacePostsDto performancePlacePostsDto = new PerformancePlacePostsDto();
        performancePlacePostsDto.setPerformancePlacePosts(performancePlacePostDtos);
        return performancePlacePostsDto;
    }

    // 공연 장소 저장
    public Long savePerformancePlacePost(PerformancePlacePostDto performancePlacePostDto) {
        PerformancePlacePost performancePlacePost = new PerformancePlacePost();

        performancePlacePost.setCategory(performancePlacePostDto.getPart());
        performancePlacePost.setCoordinate(performancePlacePostDto.getCoordinate());
        performancePlacePost.setPlace(convertToPlace(performancePlacePostDto));

        return performancePlaceRepository.save(performancePlacePost).getId();
    }

    // Place 객체 PerformancePlaceDto로부터 생성
    public Place convertToPlace(PerformancePlacePostDto performancePlacePostDto) {
        Place place = new Place();

        place.setName(performancePlacePostDto.getName());
        place.setRegion(performancePlacePostDto.getRegion());
        place.setAddress(performancePlacePostDto.getAddress());
        place.setPhoneNumber(performancePlacePostDto.getPhoneNumber());
        place.setRentalFee(performancePlacePostDto.getRentalFee());
        place.setCapacity(performancePlacePostDto.getCapacity());
        place.setHours(performancePlacePostDto.getPerformanceHours());
        place.setDescription(performancePlacePostDto.getDescription());

        return place;
    }

    // id로 PerformancePlacePost 찾는 메서드
    public PerformancePlacePostDto getPerformancePlacePost(Long postId) {
        PerformancePlacePost performancePlacePost = performancePlaceRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        return convertToPerformancePlacePostDto(performancePlacePost);
    }

    // PerformancePlacePost -> PerformancePlacePostDto 변환 메서드
    private PerformancePlacePostDto convertToPerformancePlacePostDto(PerformancePlacePost performancePlacePost) {
        PerformancePlacePostDto performancePlacePostDto = new PerformancePlacePostDto();

        performancePlacePostDto.setCoordinate(performancePlacePost.getCoordinate());
        performancePlacePostDto.setPart(performancePlacePost.getCategory());

        Place place = performancePlacePost.getPlace();

        performancePlacePostDto.setName(place.getName());
        performancePlacePostDto.setRegion(place.getRegion());
        performancePlacePostDto.setAddress(place.getAddress());
        performancePlacePostDto.setPhoneNumber(place.getPhoneNumber());
        performancePlacePostDto.setRentalFee(place.getRentalFee());
        performancePlacePostDto.setCapacity(place.getCapacity());
        performancePlacePostDto.setPerformanceHours(place.getHours());
        performancePlacePostDto.setDescription(place.getDescription());

        return performancePlacePostDto;
    }
}
