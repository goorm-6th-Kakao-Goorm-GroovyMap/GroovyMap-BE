package aespa.groovymap.freepost.service;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.FreePost;
import aespa.groovymap.domain.post.LikedPost;
import aespa.groovymap.domain.post.Post;
import aespa.groovymap.domain.post.SavedPost;
import aespa.groovymap.freepost.dto.FreePostRequestDto;
import aespa.groovymap.freepost.dto.FreePostResponseDto;
import aespa.groovymap.freepost.repository.FreePostRepository;
import aespa.groovymap.promotionpost.dto.MyListDto;
import aespa.groovymap.repository.LikedPostRepository;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.repository.SavedPostRepository;
import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import aespa.groovymap.upload.service.UpDownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FreePostService {

    private String uploadPath = "C:/upload"; // 업로드 경로

    @Value("${base.url}")
    private String baseUrl;

    private final FreePostRepository freePostRepository;
    private final UpDownService updownService;
    private final MemberRepository memberRepository;
    private final SavedPostRepository savedPostRepository;
    private final LikedPostRepository likedPostRepository;
    private final UpDownService upDownService;

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
                .author(savedFreePost.getAuthor().getNickname())
                .id(savedFreePost.getId())
                .title(savedFreePost.getTitle())
                .content(savedFreePost.getContent())
//                .part(savedFreePost.getCategory())
//                .region(savedFreePost.getRegion())
//                .coordinates(savedFreePost.getCoordinate())
                .timestamp(savedFreePost.getTimestamp())
                .likesCount(savedFreePost.getLikesCount())
                .savesCount(savedFreePost.getSavesCount())
                .viewCount(savedFreePost.getViewCount())
                .build();

        // 이미지 파일명 리스트를 생성하여 DTO에 설정
        List<String> fileNames = savedFreePost.getImageSet().stream().sorted()
                .map(boardImage -> boardImage.getFilePath()).collect(Collectors.toList());

        freePostRequestDto.setFileNames(fileNames);
        return freePostRequestDto;

    }


    // 자유 게시판 게시글 등록
    @Transactional
    public FreePostRequestDto createFreePost(FreePostResponseDto freePostResponseDto) {

        Member author =  memberRepository.findByNickname(freePostResponseDto.getAuthor())
                .orElseThrow(() -> new IllegalArgumentException("해당 작성자가 존재하지 않습니다."));

        System.out.println(author.getNickname());

        // ResponseDto로부터 Coordinate 객체 생성
//        Coordinate coordinate = freePostResponseDto.getCoordinateObject(); // 문자열을 객체로 변환
        // ResponseDto로부터 PromotionPost 객체 생성
        FreePost freePost = FreePost.builder()
                .title(freePostResponseDto.getTitle())
                .author(author)
                .content(freePostResponseDto.getContent())
//                .category(freePostResponseDto.getPart())
//                .region(freePostResponseDto.getRegion())
//                .coordinate(coordinate)
                .timestamp(ZonedDateTime.now()) // 현재 시간으로 설정
                .likesCount(0) // 초기 좋아요 수 설정
                .savesCount(0) // 초기 저장 수 설정
                .viewCount(0) // 초기 조회 수 설정
                .build();

        if (freePostResponseDto.getFileNames() != null && !freePostResponseDto.getFileNames().isEmpty()) {
            List<MultipartFile> multipartFiles = freePostResponseDto.getFileNames();
            UploadFileDto uploadFileDto = new UploadFileDto(multipartFiles);
            List<UploadResultDto> uploadResults = upDownService.uploadFiles(uploadFileDto);
            uploadResults.forEach(result -> {
                freePost.addImage(result.getFileName(), result.getFilePath(), result.getFileType());
            });
        }

        // 생성된 PromotionPost 객체를 저장
        FreePost savedFreePost = freePostRepository.save(freePost);
        System.out.println(savedFreePost);

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

    // 자유 게시판 게시글 저장
    public void savePost(Long postId, Long memberId) {
        // 자유 게시판 게시글 조회
        FreePost freePost = freePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유 게시판 게시글이 존재하지 않습니다."));

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 회원의 MemberContent가 null인 경우 초기화 -> merge하면 지워도 될듯(?)
        MemberContent memberContent = member.getMemberContent();
        if (memberContent == null) {
            memberContent = new MemberContent();
            member.setMemberContent(memberContent);
        }

        // MemberContent의 savedPosts가 null인 경우 초기화
        if (memberContent.getSavedPosts() == null) {
            memberContent.setSavedPosts(new ArrayList<>());
        }

        // 자유 게시판 게시글 저장 여부 확인
        boolean isSaved = memberContent.getSavedPosts().stream()
                .anyMatch(savedPost -> savedPost.getSavedPost().equals(freePost));

        // 이미 저장된 자유 게시판 게시글인 경우 예외를 던짐
        if (isSaved) {
            throw new IllegalArgumentException("이미 저장된 게시글입니다.");
        }

        // SavedPost 객체 생성 및 저장
        SavedPost savedPost = new SavedPost();
        savedPost.setSavedPost(freePost);
        savedPost.setSavedMemberContent(memberContent);

        // memberContent의 savedPosts 리스트에 savedPost 추가
        memberContent.getSavedPosts().add(savedPost);

        // savedPostRepository를 통해 SavedPost 객체 저장
        savedPostRepository.save(savedPost);

        // 자유 게시판 게시글의 저장 수 증가
        freePostRepository.updateSaves(postId);
    }

    // 자유 게시판 게시글 좋아요
    public void likePost(Long postId, Long memberId) {
        // 자유 게시판 게시글 조회
        FreePost freePost = freePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유 게시판 게시글이 존재하지 않습니다."));

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 회원의 MemberContent가 null인 경우 초기화
        MemberContent memberContent = member.getMemberContent();
        if (memberContent == null) {
            memberContent = new MemberContent();
            member.setMemberContent(memberContent);
        }

        // 자유 게시판 게시글 좋아요 여부 확인
        boolean isLiked = memberContent.getLikedPosts().stream()
                .anyMatch(likedPost -> likedPost.getLikedPost().equals(freePost));

        // 이미 좋아요 한 자유 게시판 게시글인 경우 예외를 던짐
        if (isLiked) {
            throw new IllegalArgumentException("이미 좋아요 한 자유 게시판 게시글입니다.");
        }

        // LikedPost 객체 생성 및 저장
        LikedPost likedPost = new LikedPost();
        likedPost.setLikedPost(freePost);
        likedPost.setLikedMemberContent(memberContent);

        // memberContent의 likedPosts 리스트에 likedPost 추가
        memberContent.getLikedPosts().add(likedPost);

        // likedPostRepository를 통해 LikedPost 객체 저장
        freePostRepository.updateLikes(postId);
    }

    // 자유 게시판 게시글 삭제
    public void deletePost(Long postId, Long memberId) {
        // 게시글 조회
        FreePost freePost = freePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유 게시판 게시글이 존재하지 않습니다."));

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 게시글 작성자와 삭제 요청자가 다른 경우 예외를 던짐
        if (!freePost.getAuthor().equals(member)) {
            throw new IllegalArgumentException("자유 게시판 게시글 작성자만 삭제할 수 있습니다.");
        }

        // 해당 글에 대한 모든 좋아요 기록 삭제
        List<LikedPost> likedPosts = likedPostRepository.findByLikedPost(freePost);
        likedPosts.forEach(likedPostRepository::delete);

        // 해당 글에 대한 모든 저장 기록 삭제
        List<SavedPost> savedPosts = savedPostRepository.findBySavedPost(freePost);
        savedPosts.forEach(savedPostRepository::delete);

        // S3에 업로드 된 파일 삭제 로직 추가
        List<String> fileNames = freePost.getImageSet().stream()
                .map(image -> image.getFileName())
                .map(fileName -> fileName.replace(baseUrl, ""))
                .collect(Collectors.toList());

        String filesToDelete = String.join(",", fileNames);
        Map<String, Boolean> deleteResults = upDownService.removeFile(filesToDelete);

        // 삭제 결과 로그 출력
        deleteResults.forEach((fileName, success) -> {
            if (success) {
                log.info("Deleted file from S3: {}", fileName);
            } else {
                log.error("Failed to delete file from S3: {}", fileName);
            }
        });

        // 자유 게시판 게시글 삭제
        freePostRepository.delete(freePost);
    }

    // 로그인한 사용자가 좋아요,저장한 자유 게시판 게시글 목록 조회
    public MyListDto getMyList(Long memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 회원의 MemberContent가 null인 경우 초기화
        MemberContent memberContent = member.getMemberContent();
        if (memberContent == null) {
            memberContent = new MemberContent();
            member.setMemberContent(memberContent);
        }

        // MemberContent의 likedPosts가 null인 경우 초기화
        if (memberContent.getLikedPosts() == null) {
            memberContent.setLikedPosts(new ArrayList<>());
        }

        // MemberContent의 savedPosts가 null인 경우 초기화
        if (memberContent.getSavedPosts() == null) {
            memberContent.setSavedPosts(new ArrayList<>());
        }

        // 좋아요한 게시글 번호 리스트 생성
        List<Long> likePostIds = memberContent.getLikedPosts().stream()
                .map(LikedPost::getLikedPost) // LikedPost 객체에서 Post 객체를 가져옴
                .filter(post -> post instanceof FreePost) // 홍보 게시판 게시글인지 확인
                .map(Post::getId)
                .collect(Collectors.toList());

        // 저장한 게시글 번호 리스트 생성
        List<Long> savePostIds = memberContent.getSavedPosts().stream()
                .map(SavedPost::getSavedPost) // SavedPost 객체에서 Post 객체를 가져옴
                .filter(post -> post instanceof FreePost) // 홍보 게시판 게시글인지 확인
                .map(Post::getId)
                .collect(Collectors.toList());

        // MyListDto 생성 및 반환
        MyListDto myListDto = new MyListDto();
        myListDto.setLikePostIds(likePostIds);
        myListDto.setSavePostIds(savePostIds);

        return myListDto;
    }
}
