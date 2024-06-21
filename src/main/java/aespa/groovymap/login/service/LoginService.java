package aespa.groovymap.login.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.login.dto.MemberInfoDto;
import aespa.groovymap.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final MemberRepository memberRepository;

    public Member login(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

    public MemberInfoDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        MemberContent memberContent = member.getMemberContent();

        return makeMemberInfoDto(member, memberContent);
    }

    private MemberInfoDto makeMemberInfoDto(Member member, MemberContent memberContent) {
        MemberInfoDto memberInfoDto = new MemberInfoDto();
        memberInfoDto.setNickname(member.getNickname());
        memberInfoDto.setProfileUrl(memberContent.getProfileImage());
        return memberInfoDto;
    }
}
