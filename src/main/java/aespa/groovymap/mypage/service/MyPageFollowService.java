package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Follow;
import aespa.groovymap.domain.Member;
import aespa.groovymap.mypage.dto.MyPageFollow.MyPageFollowDto;
import aespa.groovymap.mypage.dto.MyPageFollow.MyPageFollowResponseDto;
import aespa.groovymap.repository.FollowRepository;
import aespa.groovymap.repository.MemberRepository;
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

    public List<MyPageFollowResponseDto> getFollowers(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member nickname"));

        List<Follow> myFollowers = member.getFollowers();

        List<MyPageFollowResponseDto> myPageFollowResponseDtos
                = getMyPageFollowerResponseDtos(myFollowers);

        return myPageFollowResponseDtos;
    }

    private List<MyPageFollowResponseDto> getMyPageFollowerResponseDtos(List<Follow> myFollowers) {
        List<MyPageFollowResponseDto> myPageFollowResponseDtos = new ArrayList<>();

        for (Follow myFollower : myFollowers) {
            MyPageFollowResponseDto myPageFollowResponseDto
                    = getMyPageFollowerResponseDto(myFollower);

            myPageFollowResponseDtos.add(myPageFollowResponseDto);
        }
        return myPageFollowResponseDtos;
    }

    private MyPageFollowResponseDto getMyPageFollowerResponseDto(Follow myFollower) {
        MyPageFollowResponseDto myPageFollowResponseDto = new MyPageFollowResponseDto();

        Member myFollowerMember = myFollower.getFollowing();
        myPageFollowResponseDto.setNickname(myFollowerMember.getNickname());
        myPageFollowResponseDto.setProfileImage(myFollowerMember.getMemberContent().getProfileImage());
        return myPageFollowResponseDto;
    }

    public List<MyPageFollowResponseDto> getFollowing(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member nickname"));

        List<Follow> myFollowings = member.getFollowing();

        List<MyPageFollowResponseDto> myPageFollowResponseDtos
                = getMyPageFollowingResponseDtos(myFollowings);

        return myPageFollowResponseDtos;
    }

    private List<MyPageFollowResponseDto> getMyPageFollowingResponseDtos(List<Follow> myFollowings) {
        List<MyPageFollowResponseDto> myPageFollowResponseDtos = new ArrayList<>();

        for (Follow myFollowing : myFollowings) {
            MyPageFollowResponseDto myPageFollowResponseDto
                    = getMyPageFollowingResponseDto(myFollowing);

            myPageFollowResponseDtos.add(myPageFollowResponseDto);
        }
        return myPageFollowResponseDtos;
    }

    private MyPageFollowResponseDto getMyPageFollowingResponseDto(Follow myFollowing) {
        MyPageFollowResponseDto myPageFollowResponseDto = new MyPageFollowResponseDto();

        Member myFollowingMember = myFollowing.getFollower();
        myPageFollowResponseDto.setNickname(myFollowingMember.getNickname());
        myPageFollowResponseDto.setProfileImage(myFollowingMember.getMemberContent().getProfileImage());
        return myPageFollowResponseDto;
    }
}
