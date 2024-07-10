package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.mypage.dto.MyPageInfoDto;
import aespa.groovymap.mypage.dto.MyPageInfoUpdateRequestDto;
import aespa.groovymap.mypage.dto.MyPageInfoUpdateResponseDto;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.upload.dto.SingleFileDto;
import aespa.groovymap.upload.service.UpDownService;
import java.io.IOException;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final MemberRepository memberRepository;
    //private final FileUpload fileUpload;
    private final UpDownService upDownService;

    public MyPageInfoDto getMyPageInfo(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("마이페이지 정보 요청 api 해당 닉네임 가진 member 없음"));
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

    public MyPageInfoUpdateResponseDto updateMyPageInfo(MyPageInfoUpdateRequestDto myPageInfoUpdateRequestDto,
                                                        Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("마이페이지 정보 수정 요청한 member가 없음"));
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
                                      MemberContent memberContent) throws IOException {
        //memberContent.setProfileImage(fileUpload.saveFile(myPageInfoUpdateRequestDto.getProfileImage()));
        memberContent.setProfileImage(uploadFile(myPageInfoUpdateRequestDto.getProfileImage()));
        memberContent.setIntroduction(myPageInfoUpdateRequestDto.getIntroduction());
    }

    private void setMemberInfo(MyPageInfoUpdateRequestDto myPageInfoUpdateRequestDto, Member member) {
        member.setNickname(myPageInfoUpdateRequestDto.getNickname());
        member.setRegion(myPageInfoUpdateRequestDto.getRegion());
        member.setCategory(myPageInfoUpdateRequestDto.getPart());
        member.setType(myPageInfoUpdateRequestDto.getType());
    }

    private String uploadFile(MultipartFile profileImage) {
        SingleFileDto singleFileDto = new SingleFileDto();
        singleFileDto.setFile(profileImage);
        return upDownService.uploadSingleFile(singleFileDto).getFilePath();
    }
}
