package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Follow;
import aespa.groovymap.domain.Member;
import aespa.groovymap.mypage.dto.MyPageFollow.MyPageFollowDto;
import aespa.groovymap.repository.FollowRepository;
import aespa.groovymap.repository.MemberRepository;
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
public class MyPageFollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

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

        followRepository.save(follow);
    }

    public Boolean unfollowOtherMember(Long memberId, String nickname) {
        Member fromMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member Id"));

        Member toMember = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member nickname"));

        Boolean isFollowed = isFollowed(fromMember, toMember);

        if (isFollowed) {
            // 내 팔로우 목록 중 이 닉네임이 있음
            deleteMyFollowing(fromMember.getFollowing(), toMember);
            deleteOtherFollowers(toMember.getFollowers(), fromMember);
            return true;
        } else {
            // 내 팔로우 목록 중 이 닉네임이 없음
            return false;
        }
    }

    public Boolean deleteMyFollowing(List<Follow> following, Member toMember) {
        boolean removed = following.removeIf(follow -> {
            if (follow.getFollower().equals(toMember)) {
                return true;
            } else {
                return false;
            }
        });
        return removed;
    }

    public Boolean deleteOtherFollowers(List<Follow> followers, Member fromMember) {
        boolean removed = followers.removeIf(follow -> {
            if (follow.getFollowing().equals(fromMember)) {
                return true;
            } else {
                return false;
            }
        });
        return removed;
    }
}
