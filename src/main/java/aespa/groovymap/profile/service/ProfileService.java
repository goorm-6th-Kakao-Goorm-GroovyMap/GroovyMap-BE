package aespa.groovymap.profile.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.Profile;
import aespa.groovymap.profile.dto.ProfileDto;
import aespa.groovymap.profile.repository.ProfileRepository;
import aespa.groovymap.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    public void createProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(memberId + " 회원 정보가 존재하지 않습니다."));

        // 회원의 MemberContent가 null인 경우 초기화
        MemberContent memberContent = member.getMemberContent();
        if (memberContent == null) {
            memberContent = new MemberContent();
            member.setMemberContent(memberContent);
        }

        // 회원의 introduction이 null일 경우 초기화
        if (memberContent.getIntroduction() == null) {
            memberContent.setIntroduction("Hello I'm " + member.getNickname());
        }

        Profile profile = new Profile();
        profile.setMember(member);
        profile.setNickname(member.getNickname());
        profile.setRegion(member.getRegion());
        profile.setCategory(member.getCategory());
        profile.setIntroduction(memberContent.getIntroduction());
        profile.setProfileImage(null); // 추후 수정

        profileRepository.save(profile);
    }

    public List<ProfileDto> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProfileDto convertToDTO(Profile profile) {
        return ProfileDto.builder()
                .id(profile.getId())
                .memberId(profile.getMember().getId())
                .nickname(profile.getNickname())
                .region(profile.getRegion())
                .part(String.valueOf(profile.getCategory()))
                .introduction(profile.getIntroduction())
                .profileImage(profile.getProfileImage())
                .build();
    }
}
