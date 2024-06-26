package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.post.MyPagePerformancePost;
import aespa.groovymap.mypage.dto.MyPagePerformance.MyPagePerformanceRequestDto;
import aespa.groovymap.mypage.repository.MyPagePerformancePostRepository;
import aespa.groovymap.repository.MemberRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

    public void writeMyPagePerformance(MyPagePerformanceRequestDto myPagePerformanceRequestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        MyPagePerformancePost myPagePerformancePost = createMyPagePerformancePost(myPagePerformanceRequestDto, member);
        member.getMemberContent().getMyPagePerformancePosts().add(myPagePerformancePost);

        myPagePerformancePostRepository.save(myPagePerformancePost);
    }

    public MyPagePerformancePost createMyPagePerformancePost(MyPagePerformanceRequestDto myPagePerformanceRequestDto,
                                                             Member member) {
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

    private void setMyPerformancePostInfoAboutDto(MyPagePerformanceRequestDto myPagePerformanceRequestDto,
                                                  MyPagePerformancePost myPagePerformancePost) {
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
}
