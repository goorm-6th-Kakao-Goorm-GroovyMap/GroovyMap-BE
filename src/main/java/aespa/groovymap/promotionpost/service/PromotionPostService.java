package aespa.groovymap.promotionpost.service;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.post.PromotionPost;
import aespa.groovymap.promotionpost.dto.PromotionPostRequestDto;
import aespa.groovymap.promotionpost.dto.PromotionPostResponseDto;
import aespa.groovymap.promotionpost.global.dto.UploadResultDto;
import aespa.groovymap.promotionpost.repository.PromotionPostRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionPostService {

    private String uploadPath = "C:/upload"; // 업로드 경로

    private final PromotionPostRepository promotionPostRepository;

    // 전체 홍보게시판 게시글 조회
    public List<PromotionPostRequestDto> findAll() {
        // 모든 게시글을 조회하고, DTO로 변환하여 리스트로 반환
        return promotionPostRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 게시글 단건 조회
    public PromotionPostRequestDto readOne(Long id) {
        // ID로 게시글을 조회하고, 없을 경우 예외를 던짐
        Optional<PromotionPost> result = promotionPostRepository.findByIdWithImages(id);
        PromotionPost promotionPost = result.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        PromotionPostRequestDto promotionPostRequestDto = convertToDto(promotionPost);

        // TODO: 조회수 증가 로직 구현

        return promotionPostRequestDto;
    }

    // PromotionPost 엔티티를 PromotionPostResponseDto로 변환하는 메서드
    private PromotionPostRequestDto convertToDto(PromotionPost savedPromotionPost) {
        PromotionPostRequestDto promotionPostRequestDto = PromotionPostRequestDto.builder()
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

        promotionPostRequestDto.setFileNames(fileNames);
        return promotionPostRequestDto;

    }


    // 홍보게시판 게시글 등록
    public PromotionPostRequestDto createPromotionPost(PromotionPostResponseDto promotionPostResponseDto) {

        // RequestDto로부터 PromotionPost 객체 생성
        Coordinate coordinate = promotionPostResponseDto.getCoordinateObject(); // 문자열을 객체로 변환
        // RequestDto로부터 PromotionPost 객체 생성
        PromotionPost promotionPost = PromotionPost.builder()
                .title(promotionPostResponseDto.getTitle())
                //.author(promotionPostRequestDto.getAuthor()) // 작성자 정보는 로그인 기능 구현 후 추가
                .content(promotionPostResponseDto.getContent())
                .category(promotionPostResponseDto.getPart())
                .region(promotionPostResponseDto.getRegion())
                .coordinate(coordinate)
                .timestamp(ZonedDateTime.now()) // 현재 시간으로 설정
                .likesCount(0) // 초기 좋아요 수 설정
                .savesCount(0) // 초기 저장 수 설정
                .viewCount(0) // 초기 조회 수 설정
                .build();

        // 이미지 파일이 있을 경우 이미지 추가 로직
//        if (promotionPostRequestDto.getFileNames() != null) {
//            promotionPostRequestDto.getFileNames().forEach(fileName -> {
//                String[] arr = fileName.split("_");
//                promotionPost.addImage(arr[0], arr[1], "C:/upload/" + fileName, "image");
//            });
//        }
        if (promotionPostResponseDto.getFileNames() != null) {

            final List<UploadResultDto> list = new ArrayList<>(); // 결과를 담을 리스트

            promotionPostResponseDto.getFileNames().forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename(); // 원본 파일 이름
                log.info(originalName);

                if (originalName == null || originalName.isEmpty()) {
                    log.warn("파일 이름이 유효하지 않습니다.");
                    return; // 파일 이름이 유효하지 않으면 처리하지 않음
                }

                String uuid = UUID.randomUUID().toString(); // UUID 생성

                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName); // 저장 경로 설정

                boolean image = false; // 이미지 여부 확인

                try {
                    multipartFile.transferTo(savePath); // 파일 저장

                    // 이미지 파일인지 확인
                    if (Files.probeContentType(savePath).startsWith("image")) {
                        image = true;

                        // 이미지 추가 로직
                        promotionPost.addImage(uuid, originalName, savePath.toString(), multipartFile.getContentType());

                        // 이미지 파일이면 썸네일 생성
                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                } catch (IOException e) {
                    log.error("파일 저장 중 오류 발생: {}", e.getMessage());
                    throw new RuntimeException("파일 저장 중 오류가 발생했습니다."); // 예외 발생 시 런타임 예외 던짐
                }

                // 결과 리스트에 추가
//                list.add(UploadResultDto.builder()
//                        .fileName(uuid + "_" + originalName)
//                        .img(image)
//                        .filePath(savePath.toString())
//                        .fileType(multipartFile.getContentType())
//                        .build());
            });
        }

        // 생성된 PromotionPost 객체를 저장
        PromotionPost savedPromotionPost = promotionPostRepository.save(promotionPost);

        // 저장된 객체를 DTO로 변환하여 반환
        return convertToDto(savedPromotionPost);
    }


    // 홍보게시판 게시글 분야별 조회
    public List<PromotionPostRequestDto> findByPart(String part) {
        // String part를 Category enum으로 변환
        Category category;
        try {
            category = Category.valueOf(part.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다: " + part);
        }
        // 분야별 게시글을 조회하고, DTO로 변환하여 리스트로 반환
        return promotionPostRepository.findByCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
