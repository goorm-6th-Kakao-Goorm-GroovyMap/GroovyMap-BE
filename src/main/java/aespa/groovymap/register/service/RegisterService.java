package aespa.groovymap.register.service;

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
        return !memberRepository.findByNickname(nickname).isPresent();
    }
}
