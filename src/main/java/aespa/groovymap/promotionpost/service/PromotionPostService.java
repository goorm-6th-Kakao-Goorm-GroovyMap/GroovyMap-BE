package aespa.groovymap.promotionpost.service;

import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.post.PromotionPost;
import aespa.groovymap.promotionpost.dto.PromotionPostRequestDto;
import aespa.groovymap.promotionpost.dto.PromotionPostResponseDto;
import aespa.groovymap.promotionpost.repository.PromotionPostRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionPostService {

    private final PromotionPostRepository promotionPostRepository;

    // 전체 홍보게시판 게시글 조회
    public List<PromotionPostResponseDto> findAll() {
        // 모든 게시글을 조회하고, DTO로 변환하여 리스트로 반환
        return promotionPostRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 게시글 단건 조회
    public PromotionPostResponseDto readOne(Long id) {
        // ID로 게시글을 조회하고, 없을 경우 예외를 던짐
        Optional<PromotionPost> result = promotionPostRepository.findByIdWithImages(id);
        PromotionPost promotionPost = result.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        PromotionPostResponseDto promotionPostResponseDto = convertToDto(promotionPost);

        // TODO: 조회수 증가 로직 구현

        return promotionPostResponseDto;
    }

    // PromotionPost 엔티티를 PromotionPostResponseDto로 변환하는 메서드
    private PromotionPostResponseDto convertToDto(PromotionPost savedPromotionPost) {
        PromotionPostResponseDto promotionPostResponseDto = PromotionPostResponseDto.builder()
                .id(savedPromotionPost.getId())
                .title(savedPromotionPost.getTitle())
                .content(savedPromotionPost.getContent())
                .part(savedPromotionPost.getCategory())
                .region(savedPromotionPost.getRegion())
                .coordinates(savedPromotionPost.getCoordinate())
                .timestamp(savedPromotionPost.getTimestamp())
                .likesCount(savedPromotionPost.getLikesCount())
                .savesCount(savedPromotionPost.getSavesCount())
                .viewCount(savedPromotionPost.getViewCount())
                .build();

        // 이미지 파일명 리스트를 생성하여 DTO에 설정
        List<String> fileNames = savedPromotionPost.getImageSet().stream().sorted()
                .map(boardImage -> boardImage.getFileName()).collect(Collectors.toList());

        promotionPostResponseDto.setFileNames(fileNames);
        return promotionPostResponseDto;

    }


    // 홍보게시판 게시글 등록
    public PromotionPostResponseDto createPromotionPost(PromotionPostRequestDto promotionPostRequestDto) {

        // RequestDto로부터 PromotionPost 객체 생성
        Coordinate coordinate = promotionPostRequestDto.getCoordinateObject(); // 문자열을 객체로 변환
        // RequestDto로부터 PromotionPost 객체 생성
        PromotionPost promotionPost = PromotionPost.builder()
                .title(promotionPostRequestDto.getTitle())
                //.author(promotionPostRequestDto.getAuthor()) // 작성자 정보는 로그인 기능 구현 후 추가
                .content(promotionPostRequestDto.getContent())
                .category(promotionPostRequestDto.getPart())
                .region(promotionPostRequestDto.getRegion())
                .coordinate(coordinate)
                .timestamp(ZonedDateTime.now()) // 현재 시간으로 설정
                .likesCount(0) // 초기 좋아요 수 설정
                .savesCount(0) // 초기 저장 수 설정
                .viewCount(0) // 초기 조회 수 설정
                .build();

        // 이미지 파일이 있을 경우 이미지 추가 로직
        if (promotionPostRequestDto.getFileNames() != null) {
            promotionPostRequestDto.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                promotionPost.addImage(arr[0], arr[1], "C:/upload/" + fileName, "image");
            });
        }

        // 생성된 PromotionPost 객체를 저장
        PromotionPost savedPromotionPost = promotionPostRepository.save(promotionPost);

        // 저장된 객체를 DTO로 변환하여 반환
        return convertToDto(savedPromotionPost);
    }


}
