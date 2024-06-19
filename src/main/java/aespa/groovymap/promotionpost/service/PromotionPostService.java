package aespa.groovymap.promotionpost.service;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.PromotionPost;
import aespa.groovymap.domain.post.SavedPost;
import aespa.groovymap.promotionpost.dto.PromotionPostRequestDto;
import aespa.groovymap.promotionpost.dto.PromotionPostResponseDto;
import aespa.groovymap.promotionpost.repository.PromotionPostRepository;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.repository.SavedPostRepository;
import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import aespa.groovymap.upload.service.UpDownService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionPostService {

    private String uploadPath = "C:/upload"; // 업로드 경로

    private final PromotionPostRepository promotionPostRepository;
    private final UpDownService upDownService;
    private final MemberRepository memberRepository;
    private final SavedPostRepository savedPostRepository;

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

        // 조회수 증가 메서드 호출
        updateViews(id);
        // 변경된 조회수를 적용하여 DTO 생성
        promotionPost.setViewCount(promotionPost.getViewCount() + 1);

        PromotionPostRequestDto promotionPostRequestDto = convertToDto(promotionPost);

        return promotionPostRequestDto;
    }

    @Transactional
    public int updateViews(Long id) {
        return promotionPostRepository.updateViews(id);
    }

    // PromotionPost 엔티티를 PromotionPostRequestDto로 변환하는 메서드
    private PromotionPostRequestDto convertToDto(PromotionPost savedPromotionPost) {
        PromotionPostRequestDto promotionPostRequestDto = PromotionPostRequestDto.builder()
                .id(savedPromotionPost.getId())
                .title(savedPromotionPost.getTitle())
                .author(savedPromotionPost.getAuthor().getNickname())
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
    public PromotionPostRequestDto createPromotionPost(PromotionPostResponseDto promotionPostResponseDto,
                                                       Long memberId) {

        // 작성자 정보 조회
        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // ResponseDto로부터 Coordinate 객체 생성
        Coordinate coordinate = getCoordinateObject(promotionPostResponseDto.getCoordinates()); // 문자열을 객체로 변환
        // ResponseDto로부터 PromotionPost 객체 생성
        PromotionPost promotionPost = PromotionPost.builder()
                .title(promotionPostResponseDto.getTitle())
                .author(author)
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
        if (promotionPostResponseDto.getFileNames() != null && !promotionPostResponseDto.getFileNames().isEmpty()) {
            List<MultipartFile> multipartFiles = promotionPostResponseDto.getFileNames();
            UploadFileDto uploadFileDto = new UploadFileDto(multipartFiles);
            List<UploadResultDto> uploadResults = upDownService.uploadFiles(uploadFileDto);

            uploadResults.forEach(result -> {
                promotionPost.addImage(result.getFileName(), result.getFilePath(), result.getFileType());
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

    public Coordinate getCoordinateObject(String coordinates) {
        // JSON 문자열을 Coordinate 객체로 변환하는 로직
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(coordinates, Coordinate.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse coordinate", e);
        }
    }

    // 홍보게시판 게시글 저장
    public void savePost(Long postId, Long memberId) {
        // 게시글 조회
        PromotionPost promotionPost = promotionPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 회원의 MemberContent가 null인 경우 초기화
        MemberContent memberContent = member.getMemberContent();
        if (memberContent == null) {
            memberContent = new MemberContent();
            member.setMemberContent(memberContent);
        }

        // MemberContent의 savedPosts가 null인 경우 초기화
        if (memberContent.getSavedPosts() == null) {
            memberContent.setSavedPosts(new ArrayList<>());
        }

        // 게시글 저장 여부 확인
        boolean isSaved = memberContent.getSavedPosts().stream()
                .anyMatch(savedPost -> savedPost.getSavedPost().equals(promotionPost));

        // 이미 저장된 게시글인 경우 예외를 던짐
        if (isSaved) {
            throw new IllegalArgumentException("이미 저장된 게시글입니다.");
        }

        // SavedPost 객체 생성 및 저장
        SavedPost savedPost = new SavedPost();
        savedPost.setSavedPost(promotionPost);
        savedPost.setSavedMemberContent(memberContent);

        // memberContent의 savedPosts 리스트에 savedPost 추가
        memberContent.getSavedPosts().add(savedPost);

        // savedPostRepository를 통해 SavedPost 객체 저장
        savedPostRepository.save(savedPost);

        // 홍보 게시글의 저장 수 증가
        promotionPostRepository.updateSaves(postId);

    }
}
