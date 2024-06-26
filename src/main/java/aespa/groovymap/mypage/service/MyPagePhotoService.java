package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.MyPagePost;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoWriteDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotosDto;
import aespa.groovymap.mypage.repository.MyPagePostRepository;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.uploadutil.util.FileUpload;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPagePhotoService {

    private final MyPagePostRepository myPagePostRepository;
    private final MemberRepository memberRepository;
    private final FileUpload fileUpload;

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

    public void writeMyPagePhoto(MyPagePhotoWriteDto myPagePhotoWriteDto, Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        MyPagePost myPagePost = createMyPagePost(myPagePhotoWriteDto, member);

        myPagePostRepository.save(myPagePost);
    }

    private MyPagePost createMyPagePost(MyPagePhotoWriteDto myPagePhotoWriteDto, Member member) throws IOException {
        MyPagePost myPagePost = new MyPagePost();

        myPagePost.setAuthor(member);
        myPagePost.setMyPageMemberContent(member.getMemberContent());
        myPagePost.setLikesCount(0);
        myPagePost.setSavesCount(0);
        myPagePost.setComments(new ArrayList<>());
        myPagePost.setTimestamp(ZonedDateTime.now());
        myPagePost.setTitle("");
        myPagePost.setViewCount(0);
        myPagePost.setContent(myPagePhotoWriteDto.getText());
        myPagePost.setPhotoUrl(fileUpload.saveFile(myPagePhotoWriteDto.getImage()));

        return myPagePost;
    }
}
