package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Follow;
import aespa.groovymap.domain.Member;
import aespa.groovymap.mypage.dto.MyPageFollow.MyPageFollowDto;
import aespa.groovymap.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageFollowService {

    private final MemberRepository memberRepository;

    public MyPageFollowDto followOtherMember(Long memberId, String nickname) {
        Member fromMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member Id"));

        Member toMember = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member nickname"));

        Boolean isFollowed = isFollowed(fromMember, toMember);

        MyPageFollowDto myPageFollowDto = new MyPageFollowDto();

        if (!isFollowed) {
            connectFollow(fromMember, toMember);
            myPageFollowDto.setSuccess(true);
        } else {
            log.info("이미 팔로우 한 Member");
            myPageFollowDto.setSuccess(false);
        }

        return myPageFollowDto;
    }

    private boolean isFollowed(Member fromMember, Member toMember) {
        return fromMember.getFollowing().stream()
                .anyMatch(follow -> follow.getFollower().equals(toMember));
    }

    private void connectFollow(Member fromMember, Member toMember) {
        Follow follow = new Follow();
        follow.setFollower(toMember);
        follow.setFollowing(fromMember);

        toMember.getFollowers().add(follow);
        fromMember.getFollowing().add(follow);
    }
}
