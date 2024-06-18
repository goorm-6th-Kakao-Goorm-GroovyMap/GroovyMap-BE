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
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Profile profile = new Profile();
        profile.setMember(member);
        profile.setNickname(member.getNickname());
        profile.setRegion(member.getRegion());
        profile.setCategory(member.getCategory());

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
                .nickname(profile.getNickname())
                .region(profile.getRegion())
                .part(profile.getCategory())
                .build();
    }
}
