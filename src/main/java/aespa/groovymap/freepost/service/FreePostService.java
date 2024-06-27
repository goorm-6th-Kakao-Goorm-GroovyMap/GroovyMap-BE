package aespa.groovymap.freepost.service;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.post.FreePost;
import aespa.groovymap.freepost.dto.FreePostRequestDto;
import aespa.groovymap.freepost.dto.FreePostResponseDto;
import aespa.groovymap.freepost.repository.FreePostRepository;
import aespa.groovymap.upload.dto.UploadResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FreePostService {

    private String uploadPath = "C:/upload"; // 업로드 경로

    private final FreePostRepository freePostRepository;

    // 전체 자유 게시판 게시글 조회
    public List<FreePostRequestDto> findAll() {
        // 모든 자유 게시판 게시글을 조회하고, DTO로 변환하여 리스트로 반환
        return freePostRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 자유 게시판 게시글 단건 조회
    public FreePostRequestDto readOne(Long id) {
        // ID로 게시글을 조회하고, 없을 경우 예외를 던짐
        Optional<FreePost> result = freePostRepository.findByIdWithImages(id);
        FreePost freePost = result.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 조회수 증가 메서드 호출
        updateViews(id);
        // 변경된 조회수를 적용하여 DTO 생성
        freePost.setViewCount(freePost.getViewCount() + 1);

        FreePostRequestDto freePostRequestDto = convertToDto(freePost);

        return freePostRequestDto;
    }

    @Transactional
    public int updateViews(Long id) {
        return freePostRepository.updateViews(id);
    }

    // FreePost 엔티티를 FreePostRequestDto로 변환하는 메서드
    private FreePostRequestDto convertToDto(FreePost savedFreePost) {
        FreePostRequestDto freePostRequestDto = FreePostRequestDto.builder()
                .id(savedFreePost.getId())
                .title(savedFreePost.getTitle())
                .content(savedFreePost.getContent())
                .part(savedFreePost.getCategory())
                .region(savedFreePost.getRegion())
                .coordinates(savedFreePost.getCoordinate())
                .timestamp(savedFreePost.getTimestamp())
                .likesCount(savedFreePost.getLikesCount())
                .savesCount(savedFreePost.getSavesCount())
                .viewCount(savedFreePost.getViewCount())
                .build();

        // 이미지 파일명 리스트를 생성하여 DTO에 설정
        List<String> fileNames = savedFreePost.getImageSet().stream().sorted()
                .map(boardImage -> boardImage.getFileName()).collect(Collectors.toList());

        freePostRequestDto.setFileNames(fileNames);
        return freePostRequestDto;

    }


    // 자유 게시판 게시글 등록
    public FreePostRequestDto createFreePost(FreePostResponseDto freePostResponseDto) {

        // ResponseDto로부터 Coordinate 객체 생성
        Coordinate coordinate = freePostResponseDto.getCoordinateObject(); // 문자열을 객체로 변환
        // ResponseDto로부터 PromotionPost 객체 생성
        FreePost freePost = FreePost.builder()
                .title(freePostResponseDto.getTitle())
                //.author(freePostResponseDto.getAuthor()) // 작성자 정보는 로그인 기능 구현 후 추가
                .content(freePostResponseDto.getContent())
                .category(freePostResponseDto.getPart())
                .region(freePostResponseDto.getRegion())
                .coordinate(coordinate)
                .timestamp(ZonedDateTime.now()) // 현재 시간으로 설정
                .likesCount(0) // 초기 좋아요 수 설정
                .savesCount(0) // 초기 저장 수 설정
                .viewCount(0) // 초기 조회 수 설정
                .build();

        // 이미지 파일이 있을 경우 이미지 추가 로직
//        if (freePostResponseDto.getFileNames() != null) {
//            freePostResponseDto.getFileNames().forEach(fileName -> {
//                String[] arr = fileName.split("_");
//                freePost.addImage(arr[0], arr[1], "C:/upload/" + fileName, "image");
//            });
//        }
        if (freePostResponseDto.getFileNames() != null) {

            final List<UploadResultDto> list = new ArrayList<>(); // 결과를 담을 리스트

            freePostResponseDto.getFileNames().forEach(multipartFile -> {
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
                        freePost.addImage(uuid, originalName, savePath.toString(), multipartFile.getContentType());

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
        FreePost savedFreePost = freePostRepository.save(freePost);

        // 저장된 객체를 DTO로 변환하여 반환
        return convertToDto(savedFreePost);
    }


    // 자유 게시판 게시글 분야별 조회
    public List<FreePostRequestDto> findByPart(String part) {
        // String part를 Category enum으로 변환
        Category category;
        try {
            category = Category.valueOf(part.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다: " + part);
        }
        // 분야별 게시글을 조회하고, DTO로 변환하여 리스트로 반환
        return freePostRepository.findByCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 홍보게시판 조회수 증가 메서드
//    private void increaseViewCount(PromotionPost promotionPost) {
//        promotionPost.setViewCount(promotionPost.getViewCount() + 1);
//        promotionPostRepository.save(promotionPost); // 변경사항을 데이터베이스에 저장
//    }
}
