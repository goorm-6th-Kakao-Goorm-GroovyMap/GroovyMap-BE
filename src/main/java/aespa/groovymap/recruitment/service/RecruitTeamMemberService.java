package aespa.groovymap.recruitment.service;

import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.post.RecruitTeamMemberPost;
import aespa.groovymap.recruitment.dto.RecruitTeamMemberRequestDto;
import aespa.groovymap.recruitment.dto.RecruitTeamMemberResponseDto;
import aespa.groovymap.recruitment.repository.RecruitTeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class RecruitTeamMemberService {

    private final RecruitTeamMemberRepository recruitTeamMemberRepository;

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
//                .author(recruitTeamMemberPost.getAuthor()) // 글쓴이 설정
                .content(recruitTeamMemberPost.getContent()) // 내용 설정
                .region(recruitTeamMemberPost.getRegion()) // 활동지역명 설정
                .field(recruitTeamMemberPost.getCategory()) // 분야 설정
                .part(recruitTeamMemberPost.getType()) // 유형 설정
                .coordinates(recruitTeamMemberPost.getCoordinate()) // 좌표 설정
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
        // ResponseDto로부터 RecruitTeamMemberPost 객체 생성
        Coordinate coordinate = recruitTeamMemberResponseDto.getCoordinateObject();// 문자열을 객체로 변환
        // RequestDto로부터 RecruitTeamMemberPost 객체 생성
        RecruitTeamMemberPost recruitTeamMemberPost = RecruitTeamMemberPost.builder()
//                .author(recruitTeamMemberResponseDto.getAuthor())
                .title(recruitTeamMemberResponseDto.getTitle())
                .content(recruitTeamMemberResponseDto.getContent())
                .type(recruitTeamMemberResponseDto.getPart())
                .category(recruitTeamMemberResponseDto.getField())
                .region(recruitTeamMemberResponseDto.getRegion())
                .recruitNum(recruitTeamMemberResponseDto.getRecruitNum())
                .coordinate(coordinate)
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

    @Transactional
    public void saveRecruitTeamMemberPost(Long postId) {
        RecruitTeamMemberPost post = recruitTeamMemberRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시글 ID: " + postId));
        // 저장 작업 수행, 예를 들어 상태를 업데이트하거나 다른 테이블에 저장
        // 여기서는 상태를 "saved"로 업데이트하는 예시
        post.setStatus("saved");
        recruitTeamMemberRepository.save(post);
    }

    @Transactional
    public void addLike(Long postId) {
        recruitTeamMemberRepository.incrementLikes(postId);
    }
}
