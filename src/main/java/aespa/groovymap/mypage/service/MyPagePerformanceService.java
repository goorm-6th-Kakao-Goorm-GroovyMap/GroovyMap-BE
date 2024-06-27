package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.post.MyPagePerformancePost;
import aespa.groovymap.domain.post.Post;
import aespa.groovymap.mypage.dto.MyPagePerformance.MyPagePerformanceRequestDto;
import aespa.groovymap.mypage.dto.MyPagePerformance.MyPagePerformanceResponseDto;
import aespa.groovymap.mypage.repository.MyPagePerformancePostRepository;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.repository.PostRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPagePerformanceService {

    private final MemberRepository memberRepository;
    private final MyPagePerformancePostRepository myPagePerformancePostRepository;
    private final PostRepository postRepository;

    public void writeMyPagePerformance(MyPagePerformanceRequestDto myPagePerformanceRequestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        MyPagePerformancePost myPagePerformancePost = createMyPagePerformancePost(myPagePerformanceRequestDto, member);
        member.getMemberContent().getMyPagePerformancePosts().add(myPagePerformancePost);

        myPagePerformancePostRepository.save(myPagePerformancePost);
    }

    public MyPagePerformancePost createMyPagePerformancePost
            (MyPagePerformanceRequestDto myPagePerformanceRequestDto, Member member) {
        MyPagePerformancePost myPagePerformancePost = new MyPagePerformancePost();

        setMyPerformancePostInfoAboutMember(member, myPagePerformancePost);
        setMyPerformancePostInfoAboutDto(myPagePerformanceRequestDto, myPagePerformancePost);

        return myPagePerformancePost;
    }

    private void setMyPerformancePostInfoAboutMember(Member member, MyPagePerformancePost myPagePerformancePost) {
        myPagePerformancePost.setAuthor(member);
        myPagePerformancePost.setLikesCount(0);
        myPagePerformancePost.setSavesCount(0);
        myPagePerformancePost.setComments(new ArrayList<>());
        myPagePerformancePost.setTimestamp(ZonedDateTime.now());
        myPagePerformancePost.setTitle("");
        myPagePerformancePost.setViewCount(0);
        myPagePerformancePost.setMyPagePerformanceMemberContent(member.getMemberContent());
    }

    private void setMyPerformancePostInfoAboutDto
            (MyPagePerformanceRequestDto myPagePerformanceRequestDto, MyPagePerformancePost myPagePerformancePost) {
        myPagePerformancePost.setContent(myPagePerformanceRequestDto.getDescription());
        myPagePerformancePost.setAddress(myPagePerformancePost.getAddress());
        myPagePerformancePost.setRegion(myPagePerformancePost.getRegion());
        myPagePerformancePost.setCategory(myPagePerformanceRequestDto.getPart());
        myPagePerformancePost.setType(myPagePerformanceRequestDto.getType());
        myPagePerformancePost.setDate(myPagePerformanceRequestDto.getDate());
        myPagePerformancePost.setCoordinate(getCoordinate(myPagePerformanceRequestDto));
    }

    private Coordinate getCoordinate(MyPagePerformanceRequestDto myPagePerformanceRequestDto) {
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(myPagePerformanceRequestDto.getLatitude());
        coordinate.setLongitude(myPagePerformanceRequestDto.getLongitude());
        return coordinate;
    }

    public List<MyPagePerformanceResponseDto> getMyPagePerformances(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        List<MyPagePerformancePost> myPagePerformancePosts = member.getMemberContent().getMyPagePerformancePosts();

        return getMyPagePerformanceDtos(myPagePerformancePosts);
    }

    private List<MyPagePerformanceResponseDto> getMyPagePerformanceDtos(
            List<MyPagePerformancePost> myPagePerformancePosts) {

        List<MyPagePerformanceResponseDto> myPagePerformanceResponseDtos = new ArrayList<>();

        for (MyPagePerformancePost myPagePerformancePost : myPagePerformancePosts) {
            myPagePerformanceResponseDtos.add(makeMyPagePerformanceResponseDto(myPagePerformancePost));
        }

        return myPagePerformanceResponseDtos;
    }

    private MyPagePerformanceResponseDto makeMyPagePerformanceResponseDto(MyPagePerformancePost myPagePerformancePost) {
        MyPagePerformanceResponseDto myPagePerformanceResponseDto = new MyPagePerformanceResponseDto();

        myPagePerformanceResponseDto.setId(myPagePerformancePost.getId());
        myPagePerformanceResponseDto.setDescription(myPagePerformancePost.getContent());
        myPagePerformanceResponseDto.setAddress(myPagePerformancePost.getAddress());
        myPagePerformanceResponseDto.setDate(myPagePerformancePost.getDate());
        myPagePerformanceResponseDto.setPart(myPagePerformancePost.getCategory());
        myPagePerformanceResponseDto.setType(myPagePerformancePost.getType());
        myPagePerformanceResponseDto.setRegion(myPagePerformancePost.getRegion());
        myPagePerformanceResponseDto.setLatitude(myPagePerformancePost.getCoordinate().getLatitude());
        myPagePerformanceResponseDto.setLongitude(myPagePerformancePost.getCoordinate().getLongitude());

        return myPagePerformanceResponseDto;
    }

    public void deleteMyPagePerformance(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        if (post instanceof MyPagePerformancePost) {
            myPagePerformancePostRepository.delete((MyPagePerformancePost) post);
        }
        postRepository.delete(post);
    }
}
