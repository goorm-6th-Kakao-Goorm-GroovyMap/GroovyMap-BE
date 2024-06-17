package aespa.groovymap.register.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.register.dto.RegisterRequestDto;
import aespa.groovymap.register.dto.User;
import aespa.groovymap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {
    private final MemberRepository memberRepository;

    public Boolean nicknameCheck(String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }

    public Boolean emailCheck(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    public User register(RegisterRequestDto registerRequestDto) {
        Member member = convertRegisterRequestDtoToMember(registerRequestDto);
        Member savedMember = memberRepository.save(member);
        return convertMemberToUser(savedMember);
    }

    public Member convertRegisterRequestDtoToMember(RegisterRequestDto registerRequestDto) {
        Member member = new Member();

        member.setEmail(registerRequestDto.getEmail());
        member.setPassword(registerRequestDto.getPassword());
        member.setNickname(registerRequestDto.getNickname());
        member.setRegion(registerRequestDto.getRegion());
        member.setCategory(registerRequestDto.getPart());
        member.setType(registerRequestDto.getType());

        return member;
    }

    public User convertMemberToUser(Member member) {
        User user = new User();

        user.setId(member.getId());
        user.setEmail(member.getEmail());
        user.setNickname(member.getNickname());
        user.setRegion(member.getRegion());
        user.setPart(member.getCategory());
        user.setSubPart(member.getType());

        return user;
    }
}
