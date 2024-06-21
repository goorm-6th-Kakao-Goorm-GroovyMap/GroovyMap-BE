package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.MyPagePost;
import aespa.groovymap.mypage.dto.MyPageInfoDto;
import aespa.groovymap.mypage.dto.MyPagePhotoDto;
import aespa.groovymap.mypage.dto.MyPagePhotosDto;
import aespa.groovymap.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;

    public MyPageInfoDto getMyPageInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));
        MemberContent memberContent = member.getMemberContent();

        MyPageInfoDto myPageInfoDto = new MyPageInfoDto();

        setMemberInfo(myPageInfoDto, member);
        setMemberContentInfo(myPageInfoDto, memberContent);

        return myPageInfoDto;
    }

    private void setMemberInfo(MyPageInfoDto myPageInfoDto, Member member) {
        myPageInfoDto.setEmail(member.getEmail());
        myPageInfoDto.setNickname(member.getNickname());
        myPageInfoDto.setRegion(member.getRegion());
        myPageInfoDto.setPart(member.getCategory());
        myPageInfoDto.setType(member.getType());
        myPageInfoDto.setFollowers(member.getFollowers().size());
        myPageInfoDto.setFollowing(member.getFollowing().size());
    }

    private void setMemberContentInfo(MyPageInfoDto myPageInfoDto, MemberContent memberContent) {
        myPageInfoDto.setProfileImage(memberContent.getProfileImage());
        myPageInfoDto.setIntroduction(memberContent.getIntroduction());
    }

    public MyPagePhotosDto getMyPagePhotos(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));
        MemberContent memberContent = member.getMemberContent();

        List<MyPagePost> myPagePosts = memberContent.getMyPagePosts();
        List<MyPagePhotoDto> myPagePhotoDtos = getMyPagePhotoDtos(myPagePosts);

        MyPagePhotosDto myPagePhotosDto = new MyPagePhotosDto();
        myPagePhotosDto.setMyPagePhotoDtos(myPagePhotoDtos);

        return myPagePhotosDto;
    }

    private List<MyPagePhotoDto> getMyPagePhotoDtos(List<MyPagePost> myPagePosts) {
        List<MyPagePhotoDto> myPagePhotoDtos = new ArrayList<>();

        for (MyPagePost myPagePost : myPagePosts) {
            MyPagePhotoDto myPagePhotoDto = new MyPagePhotoDto();

            myPagePhotoDto.setId(myPagePost.getId());
            myPagePhotoDto.setPhotoUrl(myPagePost.getPhotoUrl());

            myPagePhotoDtos.add(myPagePhotoDto);
        }
        return myPagePhotoDtos;
    }


}
