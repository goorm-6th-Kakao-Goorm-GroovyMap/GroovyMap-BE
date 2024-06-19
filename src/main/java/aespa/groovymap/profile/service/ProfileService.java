package aespa.groovymap.profile.service;

import aespa.groovymap.domain.Member;
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

        Profile profile = new Profile();
        profile.setMember(member);
        profile.setNickname(member.getNickname());
        profile.setRegion(member.getRegion());
        profile.setCategory(member.getCategory());
        profile.setIntroduction(""); // 추후 수정
        profile.setProfileImage(""); // 추후 수정

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
                .part(profile.getCategory())
                .introduction(profile.getIntroduction())
                .profileImage(profile.getProfileImage())
                .build();
    }
}
