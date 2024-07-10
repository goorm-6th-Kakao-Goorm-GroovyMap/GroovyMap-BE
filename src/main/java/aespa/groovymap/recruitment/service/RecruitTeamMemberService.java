package aespa.groovymap.recruitment.service;

import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.LikedPost;
import aespa.groovymap.domain.post.Post;
import aespa.groovymap.domain.post.RecruitTeamMemberPost;
import aespa.groovymap.domain.post.SavedPost;
import aespa.groovymap.recruitment.dto.MyListDto;
import aespa.groovymap.recruitment.dto.RecruitTeamMemberRequestDto;
import aespa.groovymap.recruitment.dto.RecruitTeamMemberResponseDto;
import aespa.groovymap.recruitment.repository.RecruitTeamMemberRepository;
import aespa.groovymap.repository.LikedPostRepository;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.repository.SavedPostRepository;
import aespa.groovymap.upload.service.UpDownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RecruitTeamMemberService {

    private final RecruitTeamMemberRepository recruitTeamMemberRepository;
    private final MemberRepository memberRepository;
    private final SavedPostRepository savedPostRepository;
    private final LikedPostRepository likedPostRepository;
    private final UpDownService upDownService;

    // 팀원 모집 목록 요청
    public List<RecruitTeamMemberRequestDto> findAll() {
        // 팀원 모집 목록을 조회하고, Dto로 변환하여 리스트로 반환
        return recruitTeamMemberRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RecruitTeamMemberRequestDto convertToDto(RecruitTeamMemberPost recruitTeamMemberPost) {
        return RecruitTeamMemberRequestDto.builder()
                .id(recruitTeamMemberPost.getId()) // 아이디 설정
                .title(recruitTeamMemberPost.getTitle()) // 제목 설정
                .author(recruitTeamMemberPost.getAuthor().getNickname()) // 글쓴이 설정
                .content(recruitTeamMemberPost.getContent()) // 내용 설정
                .region(recruitTeamMemberPost.getRegion()) // 활동지역명 설정
                .field(recruitTeamMemberPost.getCategory()) // 분야 설정
                .part(recruitTeamMemberPost.getType()) // 유형 설정
//                .coordinates(recruitTeamMemberPost.getCoordinate()) // 좌표 설정
                .recruitNum(recruitTeamMemberPost.getRecruitNum()) // 모집 인원 설정
                .timeStamp(recruitTeamMemberPost.getTimestamp()) // 날짜 설정
                .viewCount(recruitTeamMemberPost.getViewCount()) // 조회수 설정
                .build(); // Dto 빌드
    }

/*
    // 지역별로 정렬된 팀원 모집 목록 요청
    public List<RecruitTeamMemberRequestDto> findAllSortedByRegion() {
        // 팀원 모집 목록을 조회하고, 지역별로 정렬한 후 Dto로 변환하여 리스트로 반환
        return recruitTeamMemberRepository.findAll().stream()
                .sorted(Comparator.comparing(RecruitTeamMemberPost::getRegion))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
*/

    // 팀원 모집 게시글 등록
    public RecruitTeamMemberRequestDto createRecruitTeamMember(RecruitTeamMemberResponseDto recruitTeamMemberResponseDto) {

        Member author = memberRepository.findByNickname(recruitTeamMemberResponseDto.getAuthor())
                .orElseThrow(() -> new IllegalArgumentException("해당 작성자가 존재하지 않습니다."));

        // ResponseDto로부터 RecruitTeamMemberPost 객체 생성
//        Coordinate coordinate = recruitTeamMemberResponseDto.getCoordinateObject();// 문자열을 객체로 변환
        // RequestDto로부터 RecruitTeamMemberPost 객체 생성
        RecruitTeamMemberPost recruitTeamMemberPost = RecruitTeamMemberPost.builder()
                .author(author)
                .title(recruitTeamMemberResponseDto.getTitle())
                .content(recruitTeamMemberResponseDto.getContent())
                .type(recruitTeamMemberResponseDto.getPart())
                .category(recruitTeamMemberResponseDto.getField())
                .region(recruitTeamMemberResponseDto.getRegion())
                .recruitNum(recruitTeamMemberResponseDto.getRecruitNum())
//                .coordinate(coordinate)
                .timestamp(ZonedDateTime.now()) // 현재 시간으로 설정
                .viewCount(0) // 초기 조회 수 설정
                .build();

        // 생성된 RecruitTeamMemberPost 객체를 저장
        RecruitTeamMemberPost savedRecruitTeamMemberPost = recruitTeamMemberRepository.save(recruitTeamMemberPost);

        // 저장된 객체를 DTO로 변환하여 반환
        return convertToDto(savedRecruitTeamMemberPost);
    }

    // 팀원 모집 게시글 단건 조회
    public RecruitTeamMemberRequestDto readOne(Long id) {
        // ID로 팀원 모집 게시글을 조회하고, 없을 경우 예외를 던짐
        Optional<RecruitTeamMemberPost> result = recruitTeamMemberRepository.findById(id);
        RecruitTeamMemberPost recruitTeamMemberPost = result.orElseThrow(() -> new IllegalArgumentException("해당 팀원 모집 게시글이 존재하지 않습니다."));

        // 조회수 증가 메서드 호출
        updateViews(id);
        // 변경된 조회수를 적용하여 Dto 생성
        recruitTeamMemberPost.setViewCount(recruitTeamMemberPost.getViewCount() + 1);

        RecruitTeamMemberRequestDto recruitTeamMemberRequestDto = convertToDto(recruitTeamMemberPost);

        return recruitTeamMemberRequestDto;
    }

    @Transactional
    public int updateViews(Long id) {
        return recruitTeamMemberRepository.updateViews(id);
    }

    // 팀원 모집 게시글 저장
    public void savePost(Long postId, Long memberId) {
        // 자유 게시판 게시글 조회
        RecruitTeamMemberPost recruitTeamMemberPost = recruitTeamMemberRepository.findById(postId)
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
                .anyMatch(savedPost -> savedPost.getSavedPost().equals(recruitTeamMemberPost));

        // 이미 저장된 자유 게시판 게시글인 경우 예외를 던짐
        if (isSaved) {
            throw new IllegalArgumentException("이미 저장된 게시글입니다.");
        }

        // SavedPost 객체 생성 및 저장
        SavedPost savedPost = new SavedPost();
        savedPost.setSavedPost(recruitTeamMemberPost);
        savedPost.setSavedMemberContent(memberContent);

        // memberContent의 savedPosts 리스트에 savedPost 추가
        memberContent.getSavedPosts().add(savedPost);

        // savedPostRepository를 통해 SavedPost 객체 저장
        savedPostRepository.save(savedPost);

        // 자유 게시판 게시글의 저장 수 증가
        recruitTeamMemberRepository.updateSaves(postId);
    }

    // 팀원 모집 게시글 좋아요
    public void likePost(Long postId, Long memberId) {
        // 자유 게시판 게시글 조회
        RecruitTeamMemberPost recruitTeamMemberPost = recruitTeamMemberRepository.findById(postId)
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

        // 팀원 모집 게시글 좋아요 여부 확인
        boolean isLiked = memberContent.getLikedPosts().stream()
                .anyMatch(likedPost -> likedPost.getLikedPost().equals(recruitTeamMemberPost));

        // 이미 좋아요 한 팀원 모집 게시글인 경우 예외를 던짐
        if (isLiked) {
            throw new IllegalArgumentException("이미 좋아요 한 자유 게시판 게시글입니다.");
        }

        // LikedPost 객체 생성 및 저장
        LikedPost likedPost = new LikedPost();
        likedPost.setLikedPost(recruitTeamMemberPost);
        likedPost.setLikedMemberContent(memberContent);

        // memberContent의 likedPosts 리스트에 likedPost 추가
        memberContent.getLikedPosts().add(likedPost);

        // likedPostRepository를 통해 LikedPost 객체 저장
        recruitTeamMemberRepository.updateLikes(postId);
    }

    // 팀원 모집 게시글 삭제
    public void deletePost(Long postId, Long memberId) {
        // 게시글 조회
        RecruitTeamMemberPost recruitTeamMemberPost = recruitTeamMemberRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유 게시판 게시글이 존재하지 않습니다."));

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 게시글 작성자와 삭제 요청자가 다른 경우 예외를 던짐
        if (!recruitTeamMemberPost.getAuthor().equals(member)) {
            throw new IllegalArgumentException("자유 게시판 게시글 작성자만 삭제할 수 있습니다.");
        }

        // 해당 글에 대한 모든 좋아요 기록 삭제
        List<LikedPost> likedPosts = likedPostRepository.findByLikedPost(recruitTeamMemberPost);
        likedPosts.forEach(likedPostRepository::delete);

        // 해당 글에 대한 모든 저장 기록 삭제
        List<SavedPost> savedPosts = savedPostRepository.findBySavedPost(recruitTeamMemberPost);
        savedPosts.forEach(savedPostRepository::delete);

        // S3에 업로드 된 파일 삭제 로직 추가
        List<String> fileNames = recruitTeamMemberPost.getImageSet().stream()
                .map(image -> image.getFileName())
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
        recruitTeamMemberRepository.delete(recruitTeamMemberPost);
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
                .filter(post -> post instanceof RecruitTeamMemberPost) // 홍보 게시판 게시글인지 확인
                .map(Post::getId)
                .collect(Collectors.toList());

        // 저장한 게시글 번호 리스트 생성
        List<Long> savePostIds = memberContent.getSavedPosts().stream()
                .map(SavedPost::getSavedPost) // SavedPost 객체에서 Post 객체를 가져옴
                .filter(post -> post instanceof RecruitTeamMemberPost) // 홍보 게시판 게시글인지 확인
                .map(Post::getId)
                .collect(Collectors.toList());

        // MyListDto 생성 및 반환
        MyListDto myListDto = new MyListDto();
        myListDto.setLikePostIds(likePostIds);
        myListDto.setSavePostIds(savePostIds);

        return myListDto;
    }
}