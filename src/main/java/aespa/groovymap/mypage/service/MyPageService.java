package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.mypage.dto.MyPageInfoDto;
import aespa.groovymap.mypage.dto.MyPageInfoUpdateRequestDto;
import aespa.groovymap.mypage.dto.MyPageInfoUpdateResponseDto;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.uploadutil.util.FileUpload;
import java.io.IOException;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final MemberRepository memberRepository;
    private final FileUpload fileUpload;

    public MyPageInfoDto getMyPageInfo(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
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

    public MyPageInfoUpdateResponseDto updateMyPageInfo(Long memberId,
                                                        MyPageInfoUpdateRequestDto myPageInfoUpdateRequestDto)
            throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));
        MemberContent memberContent = member.getMemberContent();
        log.info("membercontent id = {}", memberContent.getId());

        setMemberInfo(myPageInfoUpdateRequestDto, member);
        setMemberContentInfo(myPageInfoUpdateRequestDto, memberContent);

        return getMyPageInfoUpdateResponseDto(member, memberContent);
    }

    private MyPageInfoUpdateResponseDto getMyPageInfoUpdateResponseDto(Member member, MemberContent memberContent) {
        MyPageInfoUpdateResponseDto myPageInfoUpdateResponseDto = new MyPageInfoUpdateResponseDto();
        myPageInfoUpdateResponseDto.setProfileImage(memberContent.getProfileImage());
        myPageInfoUpdateResponseDto.setIntroduction(memberContent.getIntroduction());
        myPageInfoUpdateResponseDto.setNickname(member.getNickname());
        myPageInfoUpdateResponseDto.setRegion(member.getRegion());
        myPageInfoUpdateResponseDto.setPart(member.getCategory());
        myPageInfoUpdateResponseDto.setType(member.getType());
        return myPageInfoUpdateResponseDto;
    }

    private void setMemberContentInfo(MyPageInfoUpdateRequestDto myPageInfoUpdateRequestDto,
                                      MemberContent memberContent)
            throws IOException {
        memberContent.setProfileImage(fileUpload.saveFile(myPageInfoUpdateRequestDto.getProfileImage()));
        memberContent.setIntroduction(myPageInfoUpdateRequestDto.getIntroduction());
    }

    private void setMemberInfo(MyPageInfoUpdateRequestDto myPageInfoUpdateRequestDto, Member member) {
        member.setNickname(myPageInfoUpdateRequestDto.getNickname());
        member.setRegion(myPageInfoUpdateRequestDto.getRegion());
        member.setCategory(myPageInfoUpdateRequestDto.getPart());
        member.setType(myPageInfoUpdateRequestDto.getType());
    }
}
