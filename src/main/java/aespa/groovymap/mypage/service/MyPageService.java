package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.mypage.dto.MyPageInfoDto;
import aespa.groovymap.repository.MemberRepository;
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
}
